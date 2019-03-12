package com.yelp.android.bentosampleapp


import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.yelp.android.bento.testing.BentoInteraction
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
class MainActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun onData_atPosition2_clickOpensListViewActivity() {
        BentoInteraction.onData(anything()).atPosition(6)
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
