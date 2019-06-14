package com.yelp.android.bento.components

interface OnItemMovedCallback<T> {
    fun onItemMoved(oldIndex: Int, newIndex: Int, newData: List<T>)
}
