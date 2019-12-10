package com.yelp.android.bentosampleapp.components

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yelp.android.bento.components.NestedOuterComponentViewHolder
import com.yelp.android.bento.components.NestedViewModel
import com.yelp.android.bentosampleapp.R

class NestedOuterComponentExampleViewHolder :
        NestedOuterComponentViewHolder<NestedOuterExampleViewModel>() {

    lateinit var textView: TextView
    override val outerLayout = R.layout.nested_component_example
    override val recyclerViewId = R.id.recycler_view

    override fun inflate(parent: ViewGroup): View {
        return super.inflate(parent).apply {
            textView = findViewById(R.id.textview)
        }
    }

    override fun bind(presenter: Any?, element: NestedViewModel<NestedOuterExampleViewModel>) {
        super.bind(presenter, element)
        textView.text = element.outerComponentViewModel.text
    }
}

data class NestedOuterExampleViewModel(val text: String)