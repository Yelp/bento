package com.yelp.android.bento.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ComponentViewHolderAsyncCompat<P, T> protected constructor(
) : ComponentViewHolder<P, T>(), AsyncCompat {

    override fun onViewCreated(view: View) = Unit
}

interface AsyncCompat {

    val layoutId: Int

    fun onViewCreated(view: View) = Unit

    fun inflate(parent: ViewGroup): View {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        onViewCreated(itemView)
        return itemView
    }
}