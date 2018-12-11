package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.yelp.android.bento.core.ComponentGroup
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.RecyclerViewComponentController
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import kotlinx.android.synthetic.main.activity_recycler_view.*

class GridComponentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val componentController = RecyclerViewComponentController(recyclerView)

        componentController.addComponent(createEmbeddedListComponent())
        componentController.addComponent(createSimplePaddedListComponent(4))
        componentController.addComponent(createListComponentWithHeaderAndFooter())
    }

    private fun createSimplePaddedListComponent(columns: Int): ListComponent<out Any?, String> {
        return ListComponent<Any, String>(null, ListComponentExampleViewHolder::class.java).apply {
            toggleDivider(false)
            setData((0..9).map { "$columns:$it" })
            setTopGap(50)
            setBottomGap(50)
            numberColumns = columns
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % (numberColumns + 1) == 0) numberColumns else 1
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
