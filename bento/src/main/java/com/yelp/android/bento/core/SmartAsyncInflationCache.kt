package com.yelp.android.bento.core

private const val CACHE_SIZE_LIMIT = 30

/**
 * Cache of how many of each view holder type's were actually inflated. This is a map of a name of
 * a screen, to map of ViewHolder type's to how many view holder type's were requested.
 */
object SmartAsyncInflationCache : LinkedHashMap<String, MutableMap<Any, Int?>>() {

    fun incrementForViewHolder(name: String, viewHolderType: Any) {
        val existingMap = this.getOrPut(name) { mutableMapOf() }
        val existingCount = (existingMap[viewHolderType] ?: 0) + 1
        existingMap[viewHolderType] = existingCount
    }

    fun incrementForViewHolderIfMissing(name: String, viewHolderType: Any): Boolean {
        val existingMap = this.getOrPut(name) { mutableMapOf() }
        if (existingMap.isEmpty()) {
            val existingCount = (existingMap[viewHolderType] ?: 0) + 1
            existingMap[viewHolderType] = existingCount
            this[name] = existingMap
            return true // If it was empty, return true so we know to keep tracking this.
        }
        return false
    }

    override fun removeEldestEntry(
        eldest: MutableMap.MutableEntry<String, MutableMap<Any, Int?>>?
    ): Boolean {
        return size > CACHE_SIZE_LIMIT
    }
}
