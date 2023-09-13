package com.yelp.android.bentosampleapp

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import com.yelp.android.bento.testing.BentoInteraction
import com.yelp.android.bento.utils.BentoSettings
import com.yelp.android.bentosampleapp.rules.BeforeRule
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@LargeTest
class MainActivityTest {

    private val beforeRule = BeforeRule {
        BentoSettings.asyncInflationEnabled = false
    }
    private val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(beforeRule).around(intentsTestRule)

    @Test
    fun onData_atPosition8_clickOpensListViewActivity() {
        BentoInteraction.onData(anything()).atPosition(8).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(ListViewActivity::class.qualifiedName))
    }

    @Test
    fun onData_atPosition2_clickOpensRecyclerViewActivity() {
        BentoInteraction.onData(anything()).atPosition(2).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(RecyclerViewActivity::class.qualifiedName))
    }
}
