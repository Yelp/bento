package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.core.ComponentGroup
import com.yelp.android.bentosampleapp.components.HorizontalComponentExampleViewHolder
import com.yelp.android.bentosampleapp.components.LabeledComponent
import kotlinx.android.synthetic.main.activity_recycler_view.*

class HorizontalGridComponentsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val componentController =
                RecyclerViewComponentController(recyclerView, RecyclerView.HORIZONTAL)

        componentController.addComponent(createEmbeddedListComponent())
        componentController.addComponent(createSimplePaddedListComponent(4))
        componentController.addComponent(createListComponentWithHeaderAndFooter())
    }

    private fun createSimplePaddedListComponent(rows: Int): ListComponent<out Any?, String> {
        return ListComponent<Any, String>(null, HorizontalComponentExampleViewHolder::class.java, rows).apply {
            toggleDivider(false)
            setData((0..9).map { "$rows:$it" })
            setStartGap(50)
            setEndGap(50)
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % (numberLanes + 1) == 0) numberLanes else 1
                }
            }
        }
    }

    private fun createEmbeddedListComponent(): ComponentGroup {
        val result = ComponentGroup()
        result.addComponent(createSimplePaddedListComponent(2))
        val temp = ComponentGroup()
        temp.addComponent(createSimplePaddedListComponent(3))
        temp.addComponent(result)
        return temp
    }

    private fun createListComponentWithHeaderAndFooter(): ComponentGroup {
        val result = ComponentGroup()
        result.addComponent(LabeledComponent("Header"))
        result.addComponent(createSimplePaddedListComponent(1))
        result.addComponent(LabeledComponent("Footer"))
        return result
    }
}
