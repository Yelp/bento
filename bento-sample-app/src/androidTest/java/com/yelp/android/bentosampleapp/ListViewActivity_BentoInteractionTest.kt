package com.yelp.android.bentosampleapp


import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.yelp.android.bento.testing.BentoInteraction
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

@LargeTest
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
