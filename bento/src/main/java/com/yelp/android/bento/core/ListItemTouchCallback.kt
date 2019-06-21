package com.yelp.android.bento.core

import android.util.Log
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
        // Only allow reorder if it is within the same component.
        return component.getLowestComponentAtIndex(current.adapterPosition) ==
                component.getLowestComponentAtIndex(target.adapterPosition)
    }

    // Always return true here. We will check if the component is reorderable in getMovementFlags().
    override fun isLongPressDragEnabled() = true

    override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
    ): Int {
        Log.i("Lowest", component.getLowestComponentAtIndex(viewHolder.adapterPosition).getItem(0).toString())
        if (component.getLowestComponentAtIndex(viewHolder.adapterPosition).isReorderable) {
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
        // held over a new position, but not necessarily dropped at that location. clearView() is
        // called once the item is dropped and the animation is completed. We are then checking if
        // the user has moved the item. If they have, we call the listener. Shamelessly
        // stolen from https://stackoverflow.com/a/36275415
        if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            callback.onItemMovedPosition(dragFrom, dragTo)
        }

        dragTo = -1
        dragFrom = dragTo
    }
}

/**
 * Interface for listening for drag and drop events directly from the RecyclerView.
 */
interface OnItemMovedPositionListener {

    /**
     * Called when the user drops an item in a new position.
     * @param oldIndex The index of the item before a move.
     * @param newIndex The index of the item after it has been moved.
     */
    fun onItemMovedPosition(oldIndex: Int, newIndex: Int)
}