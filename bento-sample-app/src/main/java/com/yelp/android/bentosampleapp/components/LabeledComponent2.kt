package com.yelp.android.bentosampleapp.components

import com.yelp.android.bento.core.Component

class LabeledComponent2(private val label: String) : Component() {

    override fun getPresenter(position: Int) = Unit

    override fun getItem(position: Int) = label

    override fun getCount() = 1

    override fun getHolderType(position: Int) = LabeledComponentViewHolder2::class.java
}