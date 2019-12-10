package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.ListViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.components.NestedComponent
import com.yelp.android.bento.components.NestedViewModel
import com.yelp.android.bento.components.SimpleComponent
import com.yelp.android.bento.components.support.ListAdapterComponent
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bentosampleapp.components.AnimatedComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.NestedOuterComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.NestedInnerComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.NestedOuterExampleViewModel
import com.yelp.android.bentosampleapp.components.SimpleComponentExampleViewHolder
import kotlinx.android.synthetic.main.activity_list_view.*

class ListViewActivity : AppCompatActivity() {

    private lateinit var controller: ComponentController
    private lateinit var componentToScrollTo: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        controller =
                ListViewComponentController(listView)

        setupListView(controller)
    }

    private fun setupListView(controller: ComponentController) {
        addSimpleComponent(controller)
        addAnimatedComponent(controller)
        addListComponent(controller)
        addCarouselComponent(controller)
        addNestedComponent(controller)
        addArrayAdapterComponent(controller)
        addAnimatedComponent(controller)
        addComponentToScrollTo(controller)
        addCarouselComponent(controller)
        addArrayAdapterComponent(controller)
        addAnimatedComponent(controller)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_view, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                controller.clear()
                setupListView(controller)
                true
            }
            R.id.scroll -> {
                controller.scrollToComponent(componentToScrollTo)
                true
            }
            R.id.scroll_smooth -> {
                controller.scrollToComponent(componentToScrollTo, true)
                true
            }
            R.id.scroll_with_offset -> {
                controller.scrollToComponentWithOffset(componentToScrollTo, 100)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addSimpleComponent(controller: ComponentController) {
        val simpleComponent = SimpleComponent<Nothing>(SimpleComponentExampleViewHolder::class.java)
        controller.addComponent(simpleComponent)
    }


    private fun addListComponent(controller: ComponentController) {
        with(ListComponent(null,
                ListComponentExampleViewHolder::class.java)) {
            setStartGap(50)
            setData((1 until 42).map { "List element $it" })
            toggleDivider(false)
            controller.addComponent(this)
        }
    }

    private fun addArrayAdapterComponent(controller: ComponentController) {
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, (1 until 42).map { "ArrayAdapter element $it" })
        controller.addComponent(ListAdapterComponent(arrayAdapter))
    }

    private fun addAnimatedComponent(controller: ComponentController) {
        controller.addComponent(SimpleComponent(Unit,
                AnimatedComponentExampleViewHolder::class.java))
    }

    private fun addComponentToScrollTo(controller: ComponentController) {
        controller.addComponent(LabeledComponent("Component to scroll to").also {
            componentToScrollTo = it
        })
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
        controller.addComponent(carousel)
    }

    private fun addNestedComponent(controller: ComponentController) {
        val nestedViewModel = NestedViewModel(
                SimpleComponent<Nothing>(NestedInnerComponentExampleViewHolder::class.java),
                NestedOuterComponentExampleViewHolder::class.java,
                NestedOuterExampleViewModel("Nested component in ListView")
        )
        val nestedComponent = NestedComponent(nestedViewModel)
        controller.addComponent(nestedComponent)
    }
}