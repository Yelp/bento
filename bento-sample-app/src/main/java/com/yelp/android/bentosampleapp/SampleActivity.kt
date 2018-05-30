package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.yelp.android.bento.core.ComponentGroup
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.RecyclerViewComponentController
import com.yelp.android.bento.core.SimpleComponent
import com.yelp.android.bentosampleapp.components.AnimatedComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.SimpleComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.SimpleJavaComponentExampleViewHolder

/**
 * Main activity for displaying the different options for demonstrating Bento features.
 */
class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val componentController = RecyclerViewComponentController(recyclerView)

        val group = ComponentGroup()
        addSimpleComponent(group, false)
        group.setBottomGap(250)
        addListComponent(group)
        addSimpleComponent(group, true)
        addAnimatedComponent(group)
        componentController.addComponent(group)

        componentController.addComponent(SimpleComponent<Nothing>(
                SimpleJavaComponentExampleViewHolder::class.java))
    }

    private fun addSimpleComponent(group: ComponentGroup, hasGap: Boolean) {
        val simpleComponent = SimpleComponent<Nothing>(SimpleComponentExampleViewHolder::class.java)
        if (hasGap) {
            simpleComponent.setTopGap(500)
        }
        group.addComponent(simpleComponent)
    }

    private fun addListComponent(group: ComponentGroup) {
        val listExample = ListComponent(null, ListComponentExampleViewHolder::class.java)
        listExample.setTopGap(50)
        listExample.setData(listOf("list element 1", "list element 2", "list element 3"))
        group.addComponent(listExample)
    }

    private fun addAnimatedComponent(group: ComponentGroup) {
        group.addComponent(SimpleComponent(Unit, AnimatedComponentExampleViewHolder::class.java))
    }
}
