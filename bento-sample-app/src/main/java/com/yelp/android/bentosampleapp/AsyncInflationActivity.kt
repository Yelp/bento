package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.core.*
import com.yelp.android.bentosampleapp.components.LabeledComponent2
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolderAsync
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity for displaying the different options for demonstrating Bento features.
 */
class AsyncInflationActivity : AppCompatActivity() {

    private lateinit var componentController: RecyclerViewComponentController
    private lateinit var componentToScrollTo: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        val preInflater = LayoutPreInflater(AsyncLayoutInflater(this))
        componentController = RecyclerViewComponentController(recyclerView, preInflater)
        addListComponent(componentController)
        addComponentToScrollTo(componentController)

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
            else -> false
        }
    }

    private fun addListComponent(controller: ComponentController) {
        controller.addComponent(ListComponent(null,
                ListComponentExampleViewHolderAsync::class.java).apply {
            setData((1..20).map { "List element $it" })
        })
    }

    private fun addComponentToScrollTo(controller: ComponentController) {
        componentToScrollTo = LabeledComponent2("Component to scroll to")
        controller.addComponent(componentToScrollTo)
    }
}
