package black.bracken.neji.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class ItemOffsetDecoration(private val space: Int) : ItemDecoration() {

    constructor(
        context: Context,
        @DimenRes offsetId: Int
    ) : this(context.resources.getDimensionPixelSize(offsetId))

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = space
            left = space
            right = space
        }
    }

}
