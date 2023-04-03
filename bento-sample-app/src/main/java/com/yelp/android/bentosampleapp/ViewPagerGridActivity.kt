package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yelp.android.bento.componentcontrollers.ViewPagerComponentController
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.databinding.ActivityViewPagerBinding

class ViewPagerGridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPagerBinding

    private val controller: ComponentController by lazy {
        ViewPagerComponentController().apply { setViewPager(binding.viewPager) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % (columns + 1) == 0) columns else 1
                }
            }
        }
    }
}
