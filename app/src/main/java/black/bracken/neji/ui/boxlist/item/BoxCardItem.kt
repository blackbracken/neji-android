package black.bracken.neji.ui.boxlist.item

import android.content.Context
import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.BoxListCardBinding
import black.bracken.neji.model.document.Box
import com.xwray.groupie.viewbinding.BindableItem

class BoxCardItem(
    private val context: Context,
    private val box: Box,
    private val itemAmountInBox: Int
) : BindableItem<BoxListCardBinding>() {

    override fun getLayout() = R.layout.box_list_card

    override fun initializeViewBinding(view: View): BoxListCardBinding {
        return BoxListCardBinding.bind(view)
    }

    override fun bind(viewBinding: BoxListCardBinding, position: Int) {
        with(viewBinding) {
            textName.text = box.name
            textAmount.text =
                context.getString(R.string.box_list_card_item_amount, itemAmountInBox)
        }
    }

}