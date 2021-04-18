package black.bracken.neji.ext

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.TouchCallback

fun <I : Item<*>> RecyclerView.setOnSwipeItemToSideways(
    onSwipe: (I) -> Unit
) {
    val callback = object : TouchCallback() {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val adapter = adapter as GroupAdapter<*>
            val pos = viewHolder.adapterPosition

            adapter.removeGroupAtAdapterPosition(pos)
            @Suppress("UNCHECKED_CAST")
            onSwipe(adapter.getItem(pos) as I)
        }
    }

    ItemTouchHelper(callback).attachToRecyclerView(this)
}