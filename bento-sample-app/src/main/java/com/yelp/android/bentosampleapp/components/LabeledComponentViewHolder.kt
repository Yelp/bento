package com.yelp.android.bentosampleapp.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolder
import com.yelp.android.bentosampleapp.R

class LabeledComponentViewHolder : ComponentViewHolder<Unit, String>() {

    lateinit var label: TextView

    override fun inflate(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_component_example, parent, false)
                .also { label = it.findViewById(R.id.text) }
    }

    override fun bind(presenter: Unit, element: String) {
        label.text = element
    }
}