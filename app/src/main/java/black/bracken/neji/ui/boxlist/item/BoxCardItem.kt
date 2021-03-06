package black.bracken.neji.ui.boxlist.item

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import black.bracken.neji.R
import black.bracken.neji.databinding.BoxListCardBinding
import black.bracken.neji.model.Box
import black.bracken.neji.ui.boxlist.BoxListViewModel
import com.xwray.groupie.viewbinding.BindableItem

class BoxCardItem(
    private val context: Context,
    val box: Box,
    private val listener: BoxListViewModel.BoxListItemClickListener
) : BindableItem<BoxListCardBinding>() {

    override fun getLayout() = R.layout.box_list_card

    override fun initializeViewBinding(view: View): BoxListCardBinding {
        return BoxListCardBinding.bind(view)
    }

    override fun bind(viewBinding: BoxListCardBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { listener.onClick(box) }
            textName.text = box.name
            textAmount.text =
                context.getString(R.string.box_list_card_item_amount, box.itemTypeAmount)
        }
    }

    override fun getSwipeDirs(): Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

}