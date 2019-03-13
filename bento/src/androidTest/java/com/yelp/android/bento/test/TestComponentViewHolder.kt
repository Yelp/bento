package com.yelp.android.bento.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yelp.android.bento.core.ComponentViewHolder

class TestComponentViewHolder : ComponentViewHolder<Unit?, Unit?>() {

    override fun inflate(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false).also {
                    it.findViewById<TextView>(android.R.id.text1).text = "Test Item"
                }
    }

    override fun bind(presenter: Unit?, element: Unit?) = Unit
}