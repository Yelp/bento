package com.yelp.android.bento.core

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ListItemTouchCallback(
        private val component: ComponentGroup,
        private val callback: OnItemMovedPositionListener
) : ItemTouchHelper.Callback() {

    private var dragFrom = -1
    private var dragTo = -1

    override fun canDropOver(
            recyclerView: RecyclerView,
            current: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
        // Only allow reorder if it is within a component.
        return component.componentAt(current.adapterPosition) ==
                component.componentAt(target.adapterPosition)
    }

    override fun isLongPressDragEnabled() = true

    override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (component.componentAt(viewHolder.adapterPosition).isReorderable) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, 0)
        }
        return makeMovementFlags(0, 0)
    }

    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition


        if (dragFrom == -1) {
            dragFrom = fromPosition
        }
        dragTo = toPosition
        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // No swipe support for now.
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        // This simply checks when the user "drops" an item. onMove is called anytime the item is
        // held over a new position, but not necessarily dropped at that location. Shamelessly
        // stolen from https://stackoverflow.com/a/36275415
        if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            callback.onItemMovedPosition(dragFrom, dragTo)
        }

        dragTo = -1
        dragFrom = dragTo
    }
}

interface OnItemMovedPositionListener {
    fun onItemMovedPosition(oldIndex: Int, newIndex: Int)
}