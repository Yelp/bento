package com.yelp.android.bento.utils

import androidx.recyclerview.widget.ListUpdateCallback
import com.yelp.android.bento.core.Component

/**
 * An adapter to use [DiffUtil.dispatchUpdatesTo()] on a Bento component.
 * Usage:
 * val diff = DiffUtil.calculateDiff(...)
 * diff.dispatchUpdatesTo(ComponentUpdateCallback(yourComponent))
 */
class ComponentUpdateCallback(val component: Component) : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        component.notifyItemRangeChanged(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        component.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onInserted(position: Int, count: Int) {
        component.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        component.notifyItemRangeRemoved(position, count)
    }
}
