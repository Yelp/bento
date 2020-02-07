package com.yelp.android.bentosampleapp

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test

private const val FIRST_CAROUSEL_ELEMENT = "Carousel element 1"

class RecyclerViewScrollTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(ToggleScrollInRecyclerViewActivity::class.java)

    @Test
    fun onScrollDisabled_triesToScroll_doesNotScroll() {
        onView(withId(R.id.toggleButton))
                .perform(click())
                .check(matches(isNotChecked()))

        onView(withId(R.id.recyclerView)).perform(swipeUp())

        onView(withText(FIRST_CAROUSEL_ELEMENT))
                .check(matches(isDisplayed()))
    }

    @Test
    fun onScrollEnabled_triesToScroll_doesScroll() {
        onView(withText(FIRST_CAROUSEL_ELEMENT))
                .check(matches(isDisplayed()))
        onView(withId(R.id.toggleButton))
                .check(matches(isChecked()))

        onView(withId(R.id.recyclerView)).perform(swipeUp())

        onView(withText(FIRST_CAROUSEL_ELEMENT))
                .check(doesNotExist())
    }

    @Test
    fun onScrollDisabled_doesNotDisableOtherInteractions() {
        onView(withId(R.id.toggleButton))
                .perform(click())
                .check(matches(isNotChecked()))

        onView(allOf(
                withClassName(`is`(RecyclerView::class.qualifiedName)),
                withChild(withText(FIRST_CAROUSEL_ELEMENT))
        )).perform(swipeLeft())

        onView(withText(FIRST_CAROUSEL_ELEMENT))
                .check(doesNotExist())
    }
}