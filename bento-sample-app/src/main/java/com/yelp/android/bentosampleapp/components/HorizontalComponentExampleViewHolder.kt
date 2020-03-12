package com.yelp.android.bentosampleapp.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bentosampleapp.R

class HorizontalComponentExampleViewHolder : ComponentViewHolder<Any?, String>() {

    private lateinit var itemView: TextView

    override fun inflate(parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.horizontal_component_item_example, parent, false)
        itemView = view.findViewById(R.id.index)

        return view
    }

    override fun bind(presenter: Any?, element: String) {
        itemView.text = element
    }
}
