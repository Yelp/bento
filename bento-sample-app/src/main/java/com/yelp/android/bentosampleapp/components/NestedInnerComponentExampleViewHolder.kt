package com.yelp.android.bentosampleapp.components

import android.view.View
import android.widget.TextView
import com.yelp.android.bento.componentcontrollers.SimpleComponentViewHolder
import com.yelp.android.bentosampleapp.R

class NestedInnerComponentExampleViewHolder :
        SimpleComponentViewHolder<Nothing?>(R.layout.simple_component_example) {

    private lateinit var textView: TextView

    override fun onViewCreated(itemView: View) {
        textView = itemView.findViewById(R.id.text)
    }

    override fun bind(presenter: Nothing?) {
        textView.text = "This is a simple component nested inside a black border."
    }
}
