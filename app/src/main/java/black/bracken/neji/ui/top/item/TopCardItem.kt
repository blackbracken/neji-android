package black.bracken.neji.ui.top.item

import android.content.Context
import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.CardItemTopBinding
import black.bracken.neji.model.Region
import com.xwray.groupie.viewbinding.BindableItem

class TopCardItem(
    private val context: Context,
    private val region: Region
) : BindableItem<CardItemTopBinding>() {

    override fun getLayout() = R.layout.card_item_top

    override fun initializeViewBinding(view: View): CardItemTopBinding {
        return CardItemTopBinding.bind(view)
    }

    override fun bind(viewBinding: CardItemTopBinding, position: Int) {
        viewBinding.text.text = context.getString(R.string.top_card_name, region.name)
    }

}