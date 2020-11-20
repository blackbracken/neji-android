package black.bracken.neji.ui.top.item

import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.CardItemTopBinding
import com.xwray.groupie.viewbinding.BindableItem

// TODO: receive `Region`
class TopCardItem(val regionName: String) : BindableItem<CardItemTopBinding>() {

    override fun getLayout() = R.layout.card_item_top

    override fun initializeViewBinding(view: View): CardItemTopBinding {
        return CardItemTopBinding.bind(view)
    }

    override fun bind(viewBinding: CardItemTopBinding, position: Int) {
        viewBinding.text.text = regionName // TODO: change into a correct one
    }

}