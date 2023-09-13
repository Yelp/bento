package com.yelp.android.bentosampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bentosampleapp.components.LabeledComponent
import com.yelp.android.bentosampleapp.components.ListComponentExampleViewHolder
import com.yelp.android.bentosampleapp.databinding.ActivityToggleScrollInRecyclerViewBinding

class ToggleScrollInRecyclerViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToggleScrollInRecyclerViewBinding

    private val componentController by lazy {
        RecyclerViewComponentController(binding.recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToggleScrollInRecyclerViewBinding.inflate(layoutInflater)
            .apply { setContentView(root) }

        componentController.addComponent(CarouselComponent().apply {
            (1..40).forEach {
                addComponent(LabeledComponent("Carousel element $it"))
            }
        })
        componentController.addComponent(ListComponent(null,
                ListComponentExampleViewHolder::class.java).apply {
            setData((1..40).map { "List element $it" })
        })

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            componentController.isScrollable = isChecked
        }
    }
}
