package com.yelp.android.bentosampleapp


import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.yelp.android.bento.testing.BentoInteraction
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun onData_atPosition2_clickOpensListViewActivity() {
        BentoInteraction.onData(anything()).atPosition(2)
                .check(ViewAssertions.matches(ViewMatchers.withText("List View")))
                .perform(ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(ListViewActivity::class.qualifiedName))
    }

    @Test
    fun onData_atPosition0_clickOpensRecyclerViewActivity() {
        BentoInteraction.onData(anything()).atPosition(0).perform(ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(RecyclerViewActivity::class.qualifiedName))
    }
}
