package com.yelp.android.bentosampleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yelp.android.bento.componentcontrollers.RecyclerViewComponentController
import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.ListComponent
import com.yelp.android.bento.core.ComponentController
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bentosampleapp.databinding.ActivityMainBinding

class RecyclingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var componentController: ComponentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        componentController = RecyclerViewComponentController(binding.recyclerView)

        componentController.addComponent(CarouselComponent().apply {
            addComponent(createListComponent(0..19))
        })
        componentController.addComponent(createListComponent(20..39))
        componentController.addComponent(CarouselComponent().apply {
            addComponent(createListComponent(40..59))
        })
        componentController.addComponent(createListComponent(60..79))
    }

    private fun createListComponent(range: IntRange): ListComponent<Any?, Int> {
        return range.toList().let { cards ->
            ListComponent(null, RecycledComponentViewHolder::class.java).apply {
                setData(cards)
                toggleDivider(false)
            }
        }
    }

    class RecycledComponentViewHolder : ComponentViewHolder<Any?, Int>() {
        private lateinit var textView: TextView
        private var isRecycled = false

        override fun inflate(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context)
                    .inflate(R.layout.simple_component_example, parent, false).also {
                        textView = it.findViewById(R.id.text)
                    }
        }

        override fun bind(presenter: Any?, element: Int) {
            textView.text = getText(element)
        }

        override fun onViewRecycled() {
            super.onViewRecycled()
            isRecycled = true
        }

        private fun getText(element: Int): String {
            return if (isRecycled) {
                "Recycled $element"
            } else {
                "Non recycled $element"
            }
        }
    }
}
