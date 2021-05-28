package com.yelp.android.bentosampleapp.components

import android.view.View
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolderAsyncCompat
import com.yelp.android.bentosampleapp.R

class ListComponentExampleViewHolderAsync
    : ComponentViewHolderAsyncCompat<Nothing?, String>() {

    private lateinit var itemView: TextView

    override val layoutId = R.layout.list_component_item_example

    override fun bind(presenter: Nothing?, element: String) {
        itemView.text = element
    }

    override fun onViewCreated(view: View) {
        itemView = view.findViewById(R.id.index)
    }

}