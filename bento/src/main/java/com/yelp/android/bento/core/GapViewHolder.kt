package com.yelp.android.bento.core

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space


class GapViewHolder : ComponentViewHolder<Nothing?, Int>() {

    private lateinit var itemView: View

    override fun inflate(parent: ViewGroup): View {
        return Space(parent.context).also(::itemView::set)
    }

    override fun bind(presenter: Nothing?, gapSize: Int) {
        itemView.layoutParams = FrameLayout.LayoutParams(gapSize, gapSize)
    }
}
