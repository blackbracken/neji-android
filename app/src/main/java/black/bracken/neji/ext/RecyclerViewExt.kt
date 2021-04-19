package black.bracken.neji.ext

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.TouchCallback

fun <I : Item<*>> RecyclerView.setOnSwipeItemToSideways(
    onSwipe: (I, Int) -> Unit
) {
    val callback = object : TouchCallback() {
        private val SWIPE_MAX_ABS_X = 200f

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val adapter = adapter as GroupAdapter<*>
            val pos = viewHolder.adapterPosition

            @Suppress("UNCHECKED_CAST")
            onSwipe(adapter.getItem(pos) as I, pos)

            adapter.notifyItemChanged(pos)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val newDx = when {
                dX >= SWIPE_MAX_ABS_X -> SWIPE_MAX_ABS_X
                dX <= -SWIPE_MAX_ABS_X -> -SWIPE_MAX_ABS_X
                else -> dX
            }

            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                newDx,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    ItemTouchHelper(callback).attachToRecyclerView(this)
}