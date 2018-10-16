package com.yelp.android.bento.testing

import android.support.test.espresso.Espresso
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.v7.widget.RecyclerView
import android.view.View
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
                    viewAssertion.check(viewHolder.itemView, null)
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
