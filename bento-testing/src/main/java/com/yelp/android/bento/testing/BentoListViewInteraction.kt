package com.yelp.android.bento.testing

import android.view.View
import android.widget.FrameLayout
import android.widget.ListView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import com.yelp.android.bento.R
import org.hamcrest.Matcher

/**
 * Calling [ListView.smoothScrollToPosition] does not make the main thread appear as busy.
 * It seems necessary to add an artificial delay so that the scrolling can happen. The 100 ms delay
 * is copied from Espresso's own code.
 *
 * See [AdapterDataLoaderAction][https://android.googlesource.com/platform/frameworks/testing/+/
 * android-support-test/espresso/core/src/main/java/android/support/test/espresso/action/
 * AdapterDataLoaderAction.java#147]
 */
private const val DELAY_FOR_SCROLL_IN_MS = 100L

/**
 * [ListView] version of the [ViewInteraction].
 */
internal class BentoListViewInteraction(
    private val targetMatcher: BentoInteraction.TargetMatcher
) : ViewInteraction {

    override fun check(viewAssertion: ViewAssertion) = apply {
        Espresso.onView(targetMatcher).perform(ViewAssertionHolder(viewAssertion, targetMatcher))
    }

    override fun perform(viewAction: ViewAction) = apply {
        Espresso.onView(targetMatcher).perform(ViewActionHolder(viewAction, targetMatcher))
    }

    class ViewAssertionHolder(
        private val viewAssertion: ViewAssertion,
        private val targetMatcher: BentoInteraction.TargetMatcher
    ) : ViewAction {
        override fun getDescription(): String = "assertionOnItemAtPosition"

        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(ListView::class.java)
        }

        override fun perform(uiController: UiController, view: View) {
            (view as? ListView)?.let {
                ScrollListViewAction(targetMatcher.position).perform(uiController, view)
                viewAssertion.check(it.getViewByPosition(targetMatcher.position), null)
            }
        }
    }

    class ViewActionHolder(
        private val viewAction: ViewAction,
        private val targetMatcher: BentoInteraction.TargetMatcher
    ) : ViewAction {
        override fun getDescription(): String {
            return ("actionOnItemAtPosition performing ViewAction: ${viewAction.description}")
        }

        override fun getConstraints(): Matcher<View> {
            return viewAction.constraints
        }

        override fun perform(uiController: UiController, view: View) {
            (view as? ListView)?.let {
                ScrollListViewAction(targetMatcher.position).perform(uiController, view)
                viewAction.perform(uiController, it.getViewByPosition(targetMatcher.position))
            }
        }
    }

    class ScrollListViewAction(private val position: Int) : ViewAction {
        override fun getDescription(): String {
            return "Scrolls list view to position $position"
        }

        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(ListView::class.java)
        }

        override fun perform(uiController: UiController, view: View) {
            (view as? ListView)?.let {
                if (position >= it.firstVisiblePosition && position <= it.lastVisiblePosition) {
                    return
                }

                it.smoothScrollToPositionFromTop(position, 0, 0)
                uiController.loopMainThreadForAtLeast(DELAY_FOR_SCROLL_IN_MS)
            }
        }
    }
}

private fun ListView.getViewByPosition(pos: Int): View {
    val firstListItemPosition = firstVisiblePosition

    val childIndex = pos - firstListItemPosition
    val child = getChildAt(childIndex)
    // The BentoListAdapter wraps items into a FrameLayout to handle margins.
    return if (child is FrameLayout && child.getTag(R.id.bento_list_view_wrapper) != null) {
        child.getChildAt(0)
    } else {
        child
    }
}
