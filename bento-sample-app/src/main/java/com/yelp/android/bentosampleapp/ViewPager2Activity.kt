package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.ViewPager2ComponentController
import com.yelp.android.bento.components.SimpleComponent
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bentosampleapp.components.SimpleComponentExampleViewHolder
import kotlinx.android.synthetic.main.activity_view_pager2.*

class ViewPager2Activity : AppCompatActivity() {

    private val controller: ComponentController by lazy {
        ViewPager2ComponentController(viewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2)

        addSimpleComponent(controller, false)
        addSimpleComponent(controller, false)
        addSimpleComponent(controller, false)
        addSimpleComponent(controller, false)
    }

    private fun addSimpleComponent(controller: ComponentController, hasGap: Boolean) {
        val simpleComponent = SimpleComponent<Nothing>(
                SimpleComponentExampleViewHolder::class.java)
        if (hasGap) {
            simpleComponent.setStartGap(500)
        }
        controller.addComponent(simpleComponent)
    }
}
