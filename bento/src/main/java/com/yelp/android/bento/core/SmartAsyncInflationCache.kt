package com.yelp.android.bento.core

import kotlin.collections.LinkedHashMap

private const val CACHE_SIZE_LIMIT = 30

/**
 * Cache of how many of each view holder type's were actually inflated. This is a map of a name of
 * a screen, to map of ViewHolder type's to how many view holder type's were requested.
 */
object SmartAsyncInflationCache : LinkedHashMap<String, MutableList<Any>>() {

    fun incrementForViewHolder(name: String, viewHolderType: Any) {
        getOrPut(name) { mutableListOf() }.add(viewHolderType)
    }

    fun incrementForViewHolderIfMissing(name: String, viewHolderType: Any): Boolean {
        val existingList = getOrPut(name) { mutableListOf() }
        if (existingList.isNotEmpty() && !existingList.contains(viewHolderType)) {
            existingList.add(viewHolderType)
            return true // If it was empty, return true so we know to keep tracking this.
        }
        return false
    }

    override fun removeEldestEntry(
        eldest: MutableMap.MutableEntry<String, MutableList<Any>>?
    ): Boolean {
        return size > CACHE_SIZE_LIMIT
    }
}
