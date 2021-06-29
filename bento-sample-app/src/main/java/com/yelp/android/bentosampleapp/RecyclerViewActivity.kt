package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.*
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bentosampleapp.components.*
import kotlinx.android.synthetic.main.activity_recycler_view.*

/**
 * Main activity for displaying the different options for demonstrating Bento features.
 */
class RecyclerViewActivity : AppCompatActivity() {

    private val componentController by lazy {
        RecyclerViewComponentController(recyclerView)
    }
    private lateinit var componentToScrollTo: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        addSimpleComponent(componentController, false)
        addDogos(5, componentController)
        addListComponent(componentController)
        addSimpleComponent(componentController, true)
        addListComponent(componentController)
        addComponentToScrollTo(componentController)
        addCarouselComponent(componentController)
        addDogos(8, componentController)
        addNestedComponent(componentController)
        addListComponent(componentController)
        addAnimatedComponent(componentController)
        addDogos(19, componentController)
        addCarouselComponent(componentController)
        addLabeledComponentWithItemVisibilityListener(componentController)

        componentController.addComponent(
            SimpleComponent<Nothing>(
                SimpleJavaComponentExampleViewHolder::class.java
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.recycler_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
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
            SimpleComponentExampleViewHolder::class.java
        )
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
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        controller.addComponent(simpleComponent)
    }

    private fun addListComponent(controller: ComponentController) {
        controller.addComponent(ListComponent(
            null,
            ListComponentExampleViewHolder::class.java
        ).apply {
            setStartGap(50)
            setData((1..20).map { "List element $it" })
        })
    }

    private fun addDogos(amount: Int, controller: ComponentController) {
        repeat(amount) { index ->
            controller.addComponent(ComplexLayoutComponent("Dogo $index"))
        }
    }

    private fun addAnimatedComponent(controller: ComponentController) {
        controller.addComponent(
            SimpleComponent(
                Unit,
                AnimatedComponentExampleViewHolder::class.java
            )
        )
    }

    private fun addComponentToScrollTo(controller: ComponentController) {
        componentToScrollTo = LabeledComponent("Component to scroll to")
        controller.addComponent(componentToScrollTo)
    }

    private fun addCarouselComponent(controller: ComponentController) {
        val carousel = CarouselComponent()
        carousel.addComponent(LabeledComponent("Swipe   --->"))
        carousel.addComponent(ListComponent(
            null,
            ListComponentExampleViewHolder::class.java, 3
        ).apply {
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
                    Toast.LENGTH_SHORT
                )
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
