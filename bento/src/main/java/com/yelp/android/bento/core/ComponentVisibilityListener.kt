package com.yelp.android.bento.core

import kotlin.math.max
import kotlin.math.min

private const val NO_POSITION = -1

/**
 * When added to the recycler view, it will notify all components as long as they are visible on
 * the screen and the recycler view is being scrolled.
 *
 * There are a few known issues with this listener:
 *  * If the listener is attached to the scrollable view after items are already visible, they will
 * not be notified until the next scroll event.
 *  * If any item within a component that is already visible changes, __all__ visible items
 * will be notified.
 */
class ComponentVisibilityListener(
    private val layoutManagerHelper: LayoutManagerHelper,
    private val componentGroup: ComponentGroup
) : Component.ComponentDataObserver {
    private var previousFirst = NO_POSITION
    private var previousLast = NO_POSITION

    fun onScrolled() {
        val firstVisible = layoutManagerHelper.findFirstVisibleItemPosition()
        val lastVisible = layoutManagerHelper.findLastVisibleItemPosition()

        // If the user hasn't scrolled far enough to show or hide any views, there's nothing for
        // us to do. Bail out.
        if (firstVisible == previousFirst && lastVisible == previousLast || firstVisible == NO_POSITION || lastVisible == NO_POSITION) {
            return
        }

        // We also return if the position don't make much sense, like they are negative or the last
        // visible is smaller than the first visible, or bigger than the total number of item.
        // This would trigger IndexOutOfBounds exception.
        if (firstVisible > lastVisible || firstVisible < 0 || lastVisible >= componentGroup.span) {
            return
        }

        if (firstVisible != previousFirst) {
            componentGroup.notifyFirstItemVisibilityChanged(firstVisible)
        }

        // If we didn't have a first and last then this is the first time we are showing any
        // items and we should notify that they are all visible.
        if (previousFirst == NO_POSITION && previousLast == NO_POSITION) {
            for (i in firstVisible..lastVisible) {
                componentGroup.notifyVisibilityChange(i, true)
            }
        } else {
            // We want to iterate through the entire range of both, views that used to be
            // visible and views that are now visible, so we can notify them of their visibility
            // changes.
            val start = min(previousFirst, firstVisible)
            val end = max(previousLast, lastVisible)

            var i = start
            while (i <= end) {

                if (i < firstVisible || i > lastVisible) {
                    // We are checking for views which are no longer visible. There are 2 cases:
                    // i < firstVisible -> Views that are ABOVE that are no longer visible.
                    // i > lastVisible -> Views that are BELOW that are no longer visible.
                    // If we've removed components, we cannot notify them that their views are
                    // not visible.
                    if (i < componentGroup.span) {
                        componentGroup.notifyVisibilityChange(i, false)
                    }
                } else if (i < previousFirst || i > previousLast) {
                    // We are checking for views which are now visible. There are 2 cases:
                    // i < previousFirst -> We have scrolled UP and new views are visible.
                    // i > previousLast -> We have scrolled DOWN and new views are visible.
                    componentGroup.notifyVisibilityChange(i, true)
                } else {
                    // We are iterating through the views that used to be visible and are still
                    // visible. Since we've already notified the items at these positions, we
                    // can skip them to make the loop (a little) faster,
                    i = min(previousLast, lastVisible)
                }
                i++
            }
        }

        previousFirst = firstVisible
        previousLast = lastVisible
    }

    /**
     * Should be called when a component is added to the controller. This will check if that
     * component is immediately visible and notify it.
     *
     * @param addedComponent the component that has been added to this controller
     */
    fun onComponentAdded(addedComponent: Component) {
        // If we didn't have a first and last then we haven't shown any components yet and we
        // can bail early. Everything will be handled by onScrolled().
        if (previousFirst == NO_POSITION && previousLast == NO_POSITION) {
            return
        }

        val firstVisible = layoutManagerHelper.findFirstVisibleItemPosition()
        val lastVisible = layoutManagerHelper.findLastVisibleItemPosition()

        // If we don't have any visible items then there's nothing to notify.
        if (firstVisible == NO_POSITION || lastVisible == NO_POSITION) {
            return
        }

        val range = componentGroup.rangeOf(addedComponent) ?: throw IllegalArgumentException(
                "Component hasn't been added to this ComponentController")

        // If a component was added within the visible components, we need to notify it.
        // We want to iterate through the items that are the intersection of our visible items
        // and the items within the range of this component.
        // NOTE: Range is inclusive-exclusive so we must subtract 1 from mUpper.
        val start = max(firstVisible, range.mLower)
        val end = min(lastVisible, range.mUpper - 1)

        for (i in start..end) {
            componentGroup.notifyVisibilityChange(i, true)
        }

        previousFirst = firstVisible
        previousLast = lastVisible
    }

    /**
     * Reset the listener, so that the next time it is called, all the views are notified of
     * visibility.
     */
    fun clear() {
        previousFirst = NO_POSITION
        previousLast = NO_POSITION
    }

    /**
     * Helper method to call if the whole ComponentGroup's visibility change.
     * This is useful for nested RecyclerViews, where we need to notify "manually"
     * the ComponentGroup that it is being moved off screen.
     */
    fun onComponentGroupVisibilityChanged(isVisible: Boolean) {
        when (isVisible) {
            true -> {
                // onScrolled also takes care of notifying any visible view when no views
                // was previously notified.
                onScrolled()
            }
            false -> {
                if (previousFirst != NO_POSITION && previousLast != NO_POSITION) {
                    val firstVisible = layoutManagerHelper.findFirstVisibleItemPosition()
                    val lastVisible = layoutManagerHelper.findLastVisibleItemPosition()

                    for (i in firstVisible..lastVisible) {
                        componentGroup.notifyVisibilityChange(i, false)
                    }
                }

                clear()
            }
        }
    }

    override fun onChanged() {
        // Ignore. This is called every time any change happens, so it's not super informative of
        // what's changed. We will use the other callbacks for better clarity on the state of our
        // controller.
    }

    /**
     * Alright, this is kinda jank. Bento doesn't currently do proper diff calculation of its
     * components, this means we notify that all items within a component have changed even if only
     * one has done so. Furthermore, notifyItemRangeInserted and notifyItemRangeRemoved will not
     * have the appropriate indices that were inserted or removed. Instead, they will simply say
     * something in this range has been inserted or removed (ie. the size of the range is accurate,
     * but there is no information on the index that was added/removed).
     *
     * See javadoc on [ComponentGroup.notifyRangeUpdated] for more.
     *
     * TODO: Fix this when Bento has better diff calculation.
     */
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        val firstVisible = layoutManagerHelper.findFirstVisibleItemPosition()
        val lastVisible = layoutManagerHelper.findLastVisibleItemPosition()

        // If the changes happened outside the visible zone, we can bail early.
        if (positionStart + itemCount <= firstVisible || positionStart > lastVisible) {
            return
        }

        // TODO: Fix this when we have a better way of determining what has changed.
        // We set the first and last to nothing so the next time onScroll is called (after the next
        // layout pass) we will notify the components that are visible. Doing so now, would notify
        // the wrong components because the changed components have not yet laid out the new views.
        previousFirst = NO_POSITION
        previousLast = NO_POSITION
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
    }

    interface LayoutManagerHelper {
        fun findFirstVisibleItemPosition(): Int

        fun findLastVisibleItemPosition(): Int
    }
}
