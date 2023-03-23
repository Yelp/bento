package com.yelp.android.bento.compose

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.core.Component
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ComposeViewHolderTest {

    @get:Rule val composeTestRule = createComposeRule()

    internal data class MyViewModel(val name: String)

    internal class MyTestComponent(val name: String) : Component() {
        override fun getPresenter(position: Int) = Presenter()

        override fun getItem(position: Int) = MyViewModel(name)

        override fun getCount() = 1

        override fun getHolderType(position: Int) = MyTestViewHolder::class.java
    }

    internal class Presenter

    internal class MyTestViewHolder : ComposeViewHolder<Presenter, MyViewModel>() {

        override fun BindView(composeView: ComposeView, presenter: Presenter, element: MyViewModel) {
            composeView.setContent {
                val state by remember { mutableStateOf(element.name) }
                Text(text = state)
            }
        }
    }

    @Test
    fun `compose view holder displays view model data`() {
        val name = "Paul"
        composeTestRule.setContent {
            RecyclerView {
                RecyclerViewComponentController(it).apply {
                    addComponent(MyTestComponent(name))
                }
            }
        }
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }
}
