package com.yelp.android.bento.components

/**
 * Interface for listening to drag and drop events.
 */
interface OnItemMovedCallback<T> {

    /**
     * Called when an item has been dropped somewhere else in the list.
     * @param oldIndex The old index of the item.
     * @param newIndex The index where the item landed.
     * @param newData A new list of data after the reorder.
     */
    fun onItemMoved(oldIndex: Int, newIndex: Int, newData: List<T>)
}
