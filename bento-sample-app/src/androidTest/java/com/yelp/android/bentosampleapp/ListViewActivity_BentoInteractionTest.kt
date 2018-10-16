package com.yelp.android.bentosampleapp


import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.yelp.android.bento.testing.BentoInteraction
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListViewActivity_BentoInteractionTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(ListViewActivity::class.java)

    @Test
    fun onData_atPosition0_isDisplayed() {
        BentoInteraction.onData(Matchers.anything()).atPosition(0)
                .check(matches(isDisplayed()))
    }

    @Test
    fun onData_isStringAtPosition40_hasMatchingText() {
        BentoInteraction.onData(Matchers.instanceOf(String::class.java)).atPosition(40)
                .check(matches(hasDescendant(withText("List element 41"))))
    }

    @Test
    fun onData_inAdapterIsStringAndAtPosition38_hasMatchingText() {
        BentoInteraction.onData(Matchers.instanceOf(String::class.java))
                .inAdapterView(withId(R.id.listView))
                .atPosition(38)
                .check(matches(hasDescendant(withText("List element 39"))))
    }
}
