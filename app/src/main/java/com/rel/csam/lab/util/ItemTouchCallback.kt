package com.rel.csam.lab.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class ItemTouchCallback(var itemMoveListener: OnItemMoveListener): ItemTouchHelper.Callback() {

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        val swipeFlas = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlag, 0)
    }

    override fun onMove(viewGroup: RecyclerView, holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return itemMoveListener.onItemMove(holder.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            // Not idle, i.e. dragging or sideslipping
            viewHolder?.itemView?.translationZ = 10f
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.translationZ = 0f
    }

    interface OnItemMoveListener {
        fun onItemMove(fromPosition: Int, toPosition:Int): Boolean
    }

}