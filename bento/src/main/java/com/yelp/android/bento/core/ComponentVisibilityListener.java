package com.yelp.android.bento.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import com.yelp.android.bento.core.Component.ComponentDataObserver;
import com.yelp.android.bento.utils.AccordionList.Range;

/**
 * When added to the recycler view, it will notify all components as long as they are visible on
 * the screen and the recycler view is being scrolled.
 * <ul>
 *     There are a few known issues with this listener:
 * <li> If the listener is attached to the RecyclerView after items are already visible, they will
 * not be notified until the next scroll event.
 * <li> If any item within a component that is already visible changes, <b>all</b> visible items
 * will be notified.
 * </ul>
 */
class ComponentVisibilityListener extends OnScrollListener implements ComponentDataObserver {

    private final GridLayoutManager mLayoutManager;
    private final ComponentGroup mComponentGroup;
    private int mPreviousFirst = RecyclerView.NO_POSITION;
    private int mPreviousLast = RecyclerView.NO_POSITION;

    ComponentVisibilityListener(GridLayoutManager layoutManager, ComponentGroup componentGroup) {
        mLayoutManager = layoutManager;
        mComponentGroup = componentGroup;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mLayoutManager.findLastVisibleItemPosition();

        // If the user hasn't scrolled far enough to show or hide any views, there's nothing for
        // us to do. Bail out.
        if ((firstVisible == mPreviousFirst && lastVisible == mPreviousLast)
                || (firstVisible == RecyclerView.NO_POSITION
                || lastVisible == RecyclerView.NO_POSITION)) {
            return;
        }

        // If we didn't have a first and last then this is the first time we are showing any
        // items and we should notify that they are all visible.
        if (mPreviousFirst == RecyclerView.NO_POSITION
                && mPreviousLast == RecyclerView.NO_POSITION) {
            for (int i = firstVisible; i <= lastVisible; i++) {
                mComponentGroup.notifyVisibilityChange(i, true);
            }
        } else {
            // We want to iterate through the entire range of both, views that used to be
            // visible and views that are now visible, so we can notify them of their visibility
            // changes.
            int start = Math.min(mPreviousFirst, firstVisible);
            int end = Math.max(mPreviousLast, lastVisible);

            for (int i = start; i <= end; i++) {

                if (i < firstVisible || i > lastVisible) {
                    // We are checking for views which are no longer visible. There are 2 cases:
                    // i < firstVisible -> Views that are ABOVE that are no longer visible.
                    // i > lastVisible -> Views that are BELOW that are no longer visible.
                    // If we've removed components, we cannot notify them that their views are
                    // not visible.
                    if (i < mComponentGroup.getSpan()) {
                        mComponentGroup.notifyVisibilityChange(i, false);
                    }
                } else if (i < mPreviousFirst || i > mPreviousLast) {
                    // We are checking for views which are now visible. There are 2 cases:
                    // i < mPreviousFirst -> We have scrolled UP and new views are visible.
                    // i > mPreviousLast -> We have scrolled DOWN and new views are visible.
                    mComponentGroup.notifyVisibilityChange(i, true);
                } else {
                    // We are iterating through the views that used to be visible and are still
                    // visible. Since we've already notified the items at these positions, we
                    // can skip them to make the loop (a little) faster,
                    i = Math.min(mPreviousLast, lastVisible);
                }
            }
        }

        mPreviousFirst = firstVisible;
        mPreviousLast = lastVisible;
    }

    /**
     * Should be called when a component is added to the controller. This will check if that
     * component is immediately visible and notify it.
     *
     * @param addedComponent the component that has been added to this controller
     */
    public void onComponentAdded(@NonNull Component addedComponent) {
        // If we didn't have a first and last then we haven't shown any components yet and we
        // can bail early. Everything will be handled by onScrolled().
        if (mPreviousFirst == RecyclerView.NO_POSITION
                && mPreviousLast == RecyclerView.NO_POSITION) {
            return;
        }

        int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mLayoutManager.findLastVisibleItemPosition();

        // If we don't have any visible items then there's nothing to notify.
        if (firstVisible == RecyclerView.NO_POSITION
                || lastVisible == RecyclerView.NO_POSITION) {
            return;
        }

        Range range = mComponentGroup.rangeOf(addedComponent);
        if (range == null) {
            throw new IllegalArgumentException(
                    "Component hasn't been added to this ComponentController");
        }

        // If a component was added within the visible components, we need to notify it.
        // We want to iterate through the items that are the intersection of our visible items
        // and the items within the range of this component.
        // NOTE: Range is inclusive-exclusive so we must subtract 1 from mUpper.
        int start = Math.max(firstVisible, range.mLower);
        int end = Math.min(lastVisible, range.mUpper - 1);

        for (int i = start; i <= end; i++) {
            mComponentGroup.notifyVisibilityChange(i, true);
        }

        mPreviousFirst = firstVisible;
        mPreviousLast = lastVisible;
    }

    @Override
    public void onChanged() {
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
     * <p>
     * See javadoc on ComponentGroup.notifyRangeUpdated() for more.
     * <p>
     * TODO: Fix this when Bento has better diff calculation.
     */
    @Override
    public void onItemRangeChanged(final int positionStart, final int itemCount) {
        int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mLayoutManager.findLastVisibleItemPosition();

        // If the changes happened outside the visible zone, we can bail early.
        if (positionStart + itemCount <= firstVisible || positionStart > lastVisible) {
            return;
        }

        // TODO: Fix this when we have a better way of determining what has changed.
        // We set the first and last to nothing so the next time onScroll is called (after the next
        // layout pass) we will notify the components that are visible. Doing so now, would notify
        // the wrong components because the changed components have not yet laid out the new views.
        mPreviousFirst = RecyclerView.NO_POSITION;
        mPreviousLast = RecyclerView.NO_POSITION;
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        // Ignored. See comment above.
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        // Ignored. See comment above.
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        // Ignored. See comment above.
    }
}
