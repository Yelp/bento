package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.components.NestedComponent
import com.yelp.android.bento.components.NestedViewModel
import com.yelp.android.bento.components.SimpleComponent
import com.yelp.android.bento.core.AsyncInflationStrategy.DEFAULT
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bentosampleapp.components.AnimatedComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.NestedInnerComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.NestedOuterComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.NestedOuterExampleViewModel
import com.yelp.android.bentosampleapp.components.SimpleComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.SimpleJavaComponentExampleViewHolder
import com.yelp.android.bentosampleapp.databinding.ActivityRecyclerViewBinding

/**
 * Main activity for displaying the different options for demonstrating Bento features.
 */
class RecyclerViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerViewBinding

    private val componentController by lazy {
        RecyclerViewComponentController(binding.recyclerView).apply {
            setAsyncCacheKey("RecyclerViewActivity")
            setAsyncStrategy(DEFAULT)
            setNumberAboveTheFoldViewHolders(12)
        }
    }
    private lateinit var componentToScrollTo: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addSimpleComponent(componentController, false)
        addListComponent(componentController)
        addSimpleComponent(componentController, true)
        addListComponent(componentController)
        addComponentToScrollTo(componentController)
        addCarouselComponent(componentController)
        addNestedComponent(componentController)
        addListComponent(componentController)
        addAnimatedComponent(componentController)
        addCarouselComponent(componentController)
        addLabeledComponentWithItemVisibilityListener(componentController)

        componentController.addComponent(SimpleComponent<Nothing>(
                SimpleJavaComponentExampleViewHolder::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.recycler_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scroll -> {
                componentController.scrollToComponent(componentToScrollTo)
                true
            }
            R.id.scroll_smooth -> {
                componentController.scrollToComponent(componentToScrollTo, true)
                true
            }
            R.id.scroll_with_offset -> {
                componentController.scrollToComponentWithOffset(componentToScrollTo, 100)
                true
            }
            R.id.insert_at_zero -> {
                addSimpleComponent(componentController, false, 0)
                true
            }
            else -> false
        }
    }

    private fun addSimpleComponent(
        controller: ComponentController,
        hasGap: Boolean,
        index: Int? = null
    ) {
        val simpleComponent = SimpleComponent<Nothing>(
                SimpleComponentExampleViewHolder::class.java)
        if (hasGap) {
            simpleComponent.setStartGap(500)
        }
        if (index != null) {
            controller.addComponent(index, simpleComponent)
        } else {
            controller.addComponent(simpleComponent)
        }
    }

    private fun addLabeledComponentWithItemVisibilityListener(
        controller: ComponentController
    ) {
        val simpleComponent =
                LabeledComponent("LabeledComponent with visibility callback").apply {
                    registerItemVisibilityListener { _, isVisible ->
                        val message = if (isVisible) "visible" else "invisible"

                        Toast.makeText(
                                this@RecyclerViewActivity,
                                "SimpleComponent is $message",
                                Toast.LENGTH_SHORT)
                                .show()
                    }
                }

        controller.addComponent(simpleComponent)
    }

    private fun addListComponent(controller: ComponentController) {
        controller.addComponent(ListComponent(null,
                ListComponentExampleViewHolder::class.java).apply {
            setStartGap(50)
            setData((1..20).map { "List element $it" })
        })
    }

    private fun addAnimatedComponent(controller: ComponentController) {
        controller.addComponent(SimpleComponent(Unit,
                AnimatedComponentExampleViewHolder::class.java))
    }

    private fun addComponentToScrollTo(controller: ComponentController) {
        componentToScrollTo = LabeledComponent("Component to scroll to")
        controller.addComponent(componentToScrollTo)
    }

    private fun addCarouselComponent(controller: ComponentController) {
        val carousel = CarouselComponent()
        carousel.addComponent(LabeledComponent("Swipe   --->"))
        carousel.addComponent(ListComponent(null,
                ListComponentExampleViewHolder::class.java, 3).apply {
            toggleDivider(false)
            setData((1..20).map { "List element $it" })
        })
        carousel.addAll((1..20).map { SimpleComponent<Nothing>(SimpleComponentExampleViewHolder::class.java) })
        carousel.addComponent(LabeledComponent("Carousel Component with visibility callback").apply {
            registerItemVisibilityListener { _, isVisible ->
                val message = if (isVisible) "visible" else "invisible"

                Toast.makeText(
                        this@RecyclerViewActivity,
                        "Component becomes $message",
                        Toast.LENGTH_SHORT)
                        .show()
            }
        })

        controller.addComponent(carousel)
    }

    private fun addNestedComponent(controller: ComponentController) {
        val nestedViewModel = NestedViewModel(
                SimpleComponent<Nothing>(NestedInnerComponentExampleViewHolder::class.java),
                NestedOuterComponentExampleViewHolder::class.java,
                NestedOuterExampleViewModel("Nested component in RecyclerView")
        )
        val nestedComponent = NestedComponent(null, nestedViewModel)
        controller.addComponent(nestedComponent)
    }
}
