package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import kotlinx.android.synthetic.main.activity_toggle_scroll_in_recycler_view.*

class ToggleScrollInRecyclerViewActivity : AppCompatActivity() {
    private val componentController by lazy {
        RecyclerViewComponentController(recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toggle_scroll_in_recycler_view)

        componentController.addComponent(CarouselComponent().apply {
            (1..40).forEach {
                addComponent(LabeledComponent("Carousel element $it"))
            }
        })
        componentController.addComponent(ListComponent(null,
                ListComponentExampleViewHolder::class.java).apply {
            setData((1..40).map { "List element $it" })
        })

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            componentController.isScrollable = isChecked
        }
    }
}