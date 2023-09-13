package com.yelp.android.bentosampleapp

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.yelp.android.bento.utils.BentoSettings
import com.yelp.android.bentosampleapp.rules.BeforeRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class RecyclerViewActivityTest {

    private val beforeRule = BeforeRule {
        BentoSettings.asyncInflationEnabled = true
    }
    private val intentsTestRule = IntentsTestRule(RecyclerViewActivity::class.java)

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(beforeRule).around(intentsTestRule)

    @Test
    fun withAsyncInflationEnabled_scrollingDownDoesntCrash_andDisplaysItems() {
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(40)
        )
        onView(ViewMatchers.withText("List element 20"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
