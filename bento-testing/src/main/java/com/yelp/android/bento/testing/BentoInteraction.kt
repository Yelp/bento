package com.yelp.android.bento.testing

import android.view.View
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import com.yelp.android.bento.R
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bento.core.asItemSequence
import com.yelp.android.bento.utils.Sequenceable
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * An interface to interact with data displayed by any [ComponentController]s in Espresso tests.
 * It allows verifications and interactions with data loaded in the current screen.
 *
 * Currently only supports [RecyclerView] and [ListView].
 */
class BentoInteraction private constructor(private val dataMatcher: Matcher<out Any?>) {
    companion object {
        @JvmStatic
        fun onData(matcher: Matcher<out Any?>) = BentoInteraction(matcher)
    }

    private var adapterMatcher: Matcher<View>? = null
    private var atPosition: Int? = null

    fun inAdapterView(matcher: Matcher<View>) = apply {
        adapterMatcher = matcher
    }

    fun atPosition(position: Int) = apply {
        atPosition = position
    }

    fun check(viewAssertion: ViewAssertion): ViewInteraction {
        val targetMatcher = TargetMatcher()
        Espresso.onView(targetMatcher).check(NoOpAssertion())

        return getAssertion(targetMatcher).check(viewAssertion)
    }

    fun perform(viewAction: ViewAction): ViewInteraction {
        val targetMatcher = TargetMatcher()
        Espresso.onView(targetMatcher).check(NoOpAssertion())

        return getAssertion(targetMatcher).perform(viewAction)
    }

    private fun getAssertion(
        targetMatcher: TargetMatcher
    ): ViewInteraction {
        return when (targetMatcher.matchedView) {
            is RecyclerView -> BentoRecyclerViewInteraction(targetMatcher)
            is ListView -> BentoListViewInteraction(targetMatcher)
            else -> throw UnsupportedOperationException("Can't assert ${targetMatcher.matchedView}")
        }
    }

    inner class TargetMatcher : TypeSafeMatcher<View>() {
        private var matchedData: MatchingData? = null
        val position: Int get() = matchedData?.position ?: -1
        val matchedView: View? get() = matchedData?.view

        override fun describeTo(description: Description) {
            description.appendText(" displaying data matching: ")
            dataMatcher.describeTo(description)
            adapterMatcher?.let {
                description.appendText(" withing adapter view matching: ")
                it.describeTo(description)
            }
            atPosition?.let {
                description.appendText(" with item at position $atPosition")
            }
        }

        override fun matchesSafely(view: View): Boolean {
            if (adapterMatcher?.matches(view) == false) return false

            val matches = mutableListOf<MatchingData>()
            view.asItemSequence?.forEachIndexed { index, item ->
                if (dataMatcher.matches(item)) {
                    matches.add(MatchingData(index, item, view))
                }
            }
            if (matches.size == 0) return false

            atPosition?.let {
                if (it < matches.size) matchedData = matches[it]
                return (it < matches.size)
            }
            return if (matches.size == 1) {
                matchedData = matches[0]
                true
            } else {
                false
            }
        }

        private val View.asItemSequence: Sequence<Any?>?
            get() = when {
                this is RecyclerView && adapter is Sequenceable -> {
                    (adapter as? Sequenceable)?.asItemSequence()
                }
                this is ListView
                        && getTag(R.id.bento_list_component_controller) is ComponentController -> {
                    (getTag(R.id.bento_list_component_controller) as? ComponentController?)
                            ?.asItemSequence()
                }
                else -> null
            }
    }

    inner class MatchingData(val position: Int, val item: Any?, val view: View)
}

/**
 * Provides the primary interface for test authors to perform actions or asserts on views.
 *
 * Each interaction is associated with a view identified by a view matcher.
 */
interface ViewInteraction {
    fun check(viewAssertion: ViewAssertion): ViewInteraction
    fun perform(viewAction: ViewAction): ViewInteraction
}

private class NoOpAssertion : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
    }
}
