package com.yelp.android.bento.core

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

const val DRAG_FLAGS = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

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
        val fromComponent = component.findRangedComponentWithIndex(current.adapterPosition)
        val toComponent = component.findComponentWithIndex(target.adapterPosition)
        if (fromComponent.mValue != toComponent) {
            return false
        }

        return fromComponent.mValue.canDropItem(
                current.adapterPosition - fromComponent.mRange.mLower,
                target.adapterPosition - fromComponent.mRange.mLower)
    }

    // Always return true here. We will check if the component is reorderable in getMovementFlags().
    override fun isLongPressDragEnabled() = true

    override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
    ): Int {
        val currentIndex = viewHolder.adapterPosition
        return if (currentIndex == RecyclerView.NO_POSITION ||
                !canReorderItemAtIndex(currentIndex)) {
            makeMovementFlags(0, 0)
        } else {
            makeMovementFlags(DRAG_FLAGS, 0)
        }
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

    private fun canReorderItemAtIndex(index: Int): Boolean {
        val targetRangeValue = component.findRangedComponentWithIndex(index)
        val targetComponent = targetRangeValue.mValue
        return targetComponent.canPickUpItem(index - targetRangeValue.mRange.mLower)
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