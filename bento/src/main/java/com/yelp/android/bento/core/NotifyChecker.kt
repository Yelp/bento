package com.yelp.android.bento.core

import android.util.Log
import java.lang.ref.WeakReference

class NotifyChecker {
    private val componentsToItem: MutableMap<Component, ItemStorage> = mutableMapOf()

    fun prefetch(component: Component) {
        val items = componentsToItem.getOrPut(component) {
            ItemStorage(component.countInternal)
        }

        val result = runCatching {
            for (position in 0 until component.countInternal) {
                items[position] = component.getItemInternal(position)
            }
        }
        if (result.isFailure) {
            Log.d("ComponentGroup", "Could not prefetch $component", result.exceptionOrNull())
        }
    }

    fun save(component: Component, position: Int, item: Any?) {
        val items = componentsToItem.getOrPut(component) {
            ItemStorage(component.countInternal)
        }

        items[position] = WeakReference(item)
    }

    fun remove(component: Component) {
        componentsToItem.remove(component)
    }

    fun onChanged(component: Component) {
        val verifyChange = verifyOnChanged(component)
        Log.d("ComponentGroup", "onChanged for $component: $verifyChange")
    }

    fun onItemRangeChanged(component: Component, positionStart: Int, itemCount: Int) {
        val verifyChange = verifyItemRangeChanged(component, positionStart, itemCount)
        Log.d("ComponentGroup", "onItemRangeChanged for $component: $verifyChange")
    }

    fun onItemRangeInserted(component: Component, positionStart: Int, itemCount: Int) {
        val itemStorage = componentsToItem[component] ?: return

        itemStorage.onItemRangeInserted(positionStart, itemCount)
    }

    fun onItemRangeRemoved(component: Component, positionStart: Int, itemCount: Int) {
        val itemStorage = componentsToItem[component] ?: return

        itemStorage.onItemRangeRemoved(positionStart, itemCount)
    }

    fun onItemMoved(component: Component, fromPosition: Int, toPosition: Int) {
        val itemStorage = componentsToItem[component] ?: return

        itemStorage.onItemMoved(fromPosition, toPosition)
    }

    // Check to confirm that the whole component indeed changed.
    private fun verifyOnChanged(component: Component): NotifyCheckResult {
        val itemStorage = componentsToItem[component]
        if (itemStorage == null) {
            return NotifyCheckResult.NotEnoughData
        } else if (!itemStorage.isFullyPrefetched) {
            return NotifyCheckResult.NotEnoughData
        }

        val updatedItems = ItemStorage(component.count)

        val unchanged = mutableListOf<Int>()
        // We can also keep track of the "null"? A bunch of components use null as items,
        // like dividers, simple components, and so on.
        val undecided = mutableListOf<Int>()

        when {
            itemStorage.items.size == component.count -> {
                // Same size, let's compare items one by one.
                itemStorage.items.forEachIndexed { index, weakReference ->
                    val item: Any? = component.getItem(index)
                    updatedItems[index] = item

                    if (item == null) {
                        undecided.add(index)
                    } else if (item == weakReference?.get()) {
                        unchanged.add(index)
                    }
                }
                componentsToItem[component] = updatedItems
                return if (unchanged.size > 0) {
                    NotifyCheckResult.IncorrectChange(unchanged)
                } else {
                    NotifyCheckResult.CorrectChange
                }
            }
            itemStorage.items.size < component.count -> {
                // Let's compare the stored items, to see if it's okay to call onChanged,
                // or if we should call onItemRangeInsertedInstead
                itemStorage.items.forEachIndexed { index, weakReference ->
                    val item: Any? = component.getItem(index)
                    updatedItems[index] = item

                    if (item == null) {
                        undecided.add(index)
                    } else if (item == weakReference?.get()) {
                        unchanged.add(index)
                    }
                }
                for (index in itemStorage.items.size until component.count) {
                    val item: Any? = component.getItem(index)
                    updatedItems[index] = item
                }

                componentsToItem[component] = updatedItems

                return if (unchanged.size > 0) {
                    NotifyCheckResult.IncorrectChange(unchanged)
                } else {
                    NotifyCheckResult.CorrectChange
                }
            }
            else -> {
                // Well, the count shrunk, let's make sure that the items in the component are different
                // than the one stored. If not, it would be better to call OnItemRangeChange instead.
                for (index in 0 until component.count) {
                    val weakReference: WeakReference<*>? = itemStorage[index]
                    val item: Any? = component.getItem(index)
                    updatedItems[index] = item
                    if (item == null) {
                        undecided.add(index)
                    } else if (item == weakReference?.get()) {
                        unchanged.add(index)
                    }
                }

                componentsToItem[component] = updatedItems

                return if (unchanged.size > 0) {
                    NotifyCheckResult.IncorrectChange(unchanged)
                } else {
                    NotifyCheckResult.CorrectChange
                }
            }
        }
    }

    private fun verifyItemRangeChanged(component: Component, positionStart: Int, itemCount: Int): NotifyCheckResult {
        val itemStorage = componentsToItem[component]
        if (itemStorage == null) {
            return NotifyCheckResult.NotEnoughData
        } else if (!itemStorage.isFullyPrefetched) {
            return NotifyCheckResult.NotEnoughData
        }

        val unchanged = mutableListOf<Int>()
        val undecided = mutableListOf<Int>()
        val updatedItems: Array<WeakReference<*>?> = arrayOf(*itemStorage.items)

        for (index in positionStart until positionStart + itemCount) {
            val weakReference: WeakReference<*>? = itemStorage[index]
            val item: Any? = component.getItem(index)
            updatedItems[index] = WeakReference(item)

            if (item == null) {
                undecided.add(index)
            } else if (item == weakReference?.get()) {
                unchanged.add(index)
            }
        }
        itemStorage.items = updatedItems
        return if (unchanged.size > 0) {
            NotifyCheckResult.IncorrectChange(unchanged)
        } else {
            NotifyCheckResult.CorrectChange
        }
    }

    class ItemStorage(capacity: Int) {
        var items: Array<WeakReference<*>?> = Array(capacity) { null }

        /**
         * Are all the item pre-fetched? Check that the items array is full of weak references.
         */
        val isFullyPrefetched: Boolean get() = items.none { it == null }

        operator fun set(index: Int, item: Any?) {
            items[index] = WeakReference(item)
        }

        operator fun get(index: Int) = items[index]

        fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            val newItems: Array<WeakReference<*>?> = Array(items.size + itemCount) { null }
            items.copyInto(newItems, 0, 0, positionStart)
            items.copyInto(newItems, positionStart + itemCount, positionStart, items.size)

            items = newItems
        }

        fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            val newItems: Array<WeakReference<*>?> = Array(items.size - itemCount) { null }
            items.copyInto(newItems, 0, 0, positionStart)
            items.copyInto(newItems, positionStart, positionStart + itemCount, items.size)

            items = newItems
        }

        fun onItemMoved(fromPosition: Int, toPosition: Int) {
            val itemsAsList = items.toMutableList()
            val item: WeakReference<*>? = itemsAsList.removeAt(fromPosition)
            itemsAsList.add(toPosition, item)

            items = itemsAsList.toTypedArray()
        }
    }
}

sealed class NotifyCheckResult {
    object NotEnoughData : NotifyCheckResult() {
        override fun toString(): String {
            return "NotifyCheckResult: Not enough data to establish validity of data change"
        }
    }

    class IncorrectChange(val unchanged: List<Int>) : NotifyCheckResult() {
        override fun toString(): String {
            return "NotifyCheckResult: You should not call onChange, indexes $unchanged stayed the same."
        }
    }

    object CorrectChange : NotifyCheckResult() {
        override fun toString(): String {
            return "NotifyCheckResult: The onChange call was justified"
        }
    }
}
