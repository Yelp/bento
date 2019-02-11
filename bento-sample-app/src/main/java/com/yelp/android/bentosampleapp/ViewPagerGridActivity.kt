package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bento.core.ListComponent
import com.yelp.android.bento.core.ViewPagerComponentController
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerGridActivity : AppCompatActivity() {

    private val controller: ComponentController by lazy {
        ViewPagerComponentController().apply { setViewPager(viewPager) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_pager)

        controller.addComponent(createListComponent(1))
        controller.addComponent(createListComponent(2))
        controller.addComponent(createListComponent(3))
    }

    private fun createListComponent(columns: Int): ListComponent<*, String> {
        return ListComponent(null,
                ListComponentExampleViewHolder::class.java, columns).apply {
            setStartGap(50)
            setData((1..100).map { "$it" })
            toggleDivider(false)
            spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % (columns + 1) == 0) columns else 1
                }
            }
        }
    }
}