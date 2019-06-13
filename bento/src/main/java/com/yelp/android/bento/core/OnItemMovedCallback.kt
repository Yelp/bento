package com.yelp.android.bento.core

interface OnItemMovedCallback {
    fun onItemMoved(component: Component, oldIndex: Int, newIndex: Int)
}
