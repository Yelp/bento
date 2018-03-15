package com.yelp.android.bentosampleapp.components

import android.view.View
import android.widget.TextView
import com.yelp.android.bento.core.SimpleComponentViewHolder
import com.yelp.android.bentosampleapp.R

class SimpleComponentExampleViewHolder : SimpleComponentViewHolder<Unit>(R.layout.simple_component_example) {

   private lateinit var textView : TextView

    override fun onViewCreated(itemView: View) {
        textView = itemView.findViewById(R.id.text)
    }

    override fun bind(presenter: Unit, element: Void?) {
        super.bind(presenter, element)
        textView.text = "This is a simple component."
    }
}