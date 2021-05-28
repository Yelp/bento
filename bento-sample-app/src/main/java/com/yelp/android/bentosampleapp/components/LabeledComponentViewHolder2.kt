package com.yelp.android.bentosampleapp.components

import android.view.View
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolderAsyncCompat
import com.yelp.android.bentosampleapp.R

class LabeledComponentViewHolder2 : ComponentViewHolderAsyncCompat<Unit, String>() {

    lateinit var label: TextView

    override fun onViewCreated(view: View) {
        label = view.findViewById(R.id.text)
    }
    override fun bind(presenter: Unit, element: String) {
        label.text = element
    }

    override val layoutId: Int = R.layout.simple_component_example
}