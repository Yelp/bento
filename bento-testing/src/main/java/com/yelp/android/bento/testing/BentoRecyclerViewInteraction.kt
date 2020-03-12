package com.yelp.android.bento.testing

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.contrib.RecyclerViewActions
import org.hamcrest.Matcher

/**
 * [RecyclerView] version of the [ViewInteraction].
 */
internal class BentoRecyclerViewInteraction(
    private val targetMatcher: BentoInteraction.TargetMatcher
) : ViewInteraction {
    override fun check(viewAssertion: ViewAssertion): ViewInteraction = apply {
        Espresso.onView(targetMatcher)
                .perform(ViewAssertionHolder(viewAssertion, targetMatcher))
    }

    override fun perform(viewAction: ViewAction): ViewInteraction = apply {
        Espresso.onView(targetMatcher)
                .perform(ViewActionHolder(viewAction, targetMatcher))
    }

    class ViewAssertionHolder(
        private val viewAssertion: ViewAssertion,
        private val targetMatcher: BentoInteraction.TargetMatcher
    ) : ViewAction {
        override fun getDescription(): String {
            return ""
        }

        override fun getConstraints(): Matcher<View> {
            return targetMatcher
        }

        override fun perform(uiController: UiController, view: View) {
            when (view) {
                is RecyclerView -> {
                    view.scrollToPosition(targetMatcher.position)
                    uiController.loopMainThreadUntilIdle()

                    val viewHolder =
                            view.findViewHolderForAdapterPosition(targetMatcher.position)
                    viewAssertion.check(viewHolder?.itemView, null)
                }
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
            when (view) {
                is RecyclerView -> {
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            targetMatcher.position,
                            viewAction).perform(uiController, view)
                    uiController.loopMainThreadUntilIdle()
                }
            }
        }
    }
}
