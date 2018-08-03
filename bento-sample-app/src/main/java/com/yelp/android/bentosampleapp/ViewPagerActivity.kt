package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.yelp.android.bento.core.Component
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.SimpleComponent
import com.yelp.android.bento.core.ViewPagerComponentController
import com.yelp.android.bentosampleapp.components.AnimatedComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.SimpleComponentExampleViewHolder
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {

    private val controller: ComponentController by lazy {
        ViewPagerComponentController().apply { setViewPager(viewPager) }
    }
    private lateinit var componentToScrollTo: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        addSimpleComponent(controller, false)
        addListComponent(controller)
        addSimpleComponent(controller, true)
        addListComponent(controller)
        addComponentToScrollTo(controller)
        addListComponent(controller)
        addAnimatedComponent(controller)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.view_pager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.scroll -> {
                controller.scrollToComponent(componentToScrollTo)
                true
            }
            R.id.scroll_smooth -> {
                controller.scrollToComponent(componentToScrollTo, true)
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
