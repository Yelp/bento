package com.yelp.android.bento.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TestComponentViewHolder : ComponentViewHolder<Nothing?, Nothing?>() {

    override fun inflate(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false).also {
                    it.findViewById<TextView>(android.R.id.text1).text = "Test Item"
                }
    }

    override fun bind(presenter: Nothing?, element: Nothing?) = Unit
}
