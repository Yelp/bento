package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.RecyclerViewComponentController
import com.yelp.android.bento.core.SimpleComponent
import com.yelp.android.bentosampleapp.components.AnimatedComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.SimpleComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.SimpleJavaComponentExampleViewHolder
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
        addListComponent(componentController)
        addSimpleComponent(componentController, true)
        addListComponent(componentController)
        addComponentToScrollTo(componentController)
        addListComponent(componentController)
        addAnimatedComponent(componentController)

        componentController.addComponent(SimpleComponent<Nothing>(
                SimpleJavaComponentExampleViewHolder::class.java))
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
            else -> false
        }
    }

    private fun addSimpleComponent(controller: ComponentController, hasGap: Boolean) {
        val simpleComponent = SimpleComponent<Nothing>(SimpleComponentExampleViewHolder::class.java)
        if (hasGap) {
            simpleComponent.setTopGap(500)
        }
        controller.addComponent(simpleComponent)
    }

    private fun addListComponent(controller: ComponentController) {
        controller.addComponent(ListComponent(null,
                ListComponentExampleViewHolder::class.java).apply {
            setTopGap(50)
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
}
