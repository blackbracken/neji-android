package black.bracken.neji.ui.itemlist.item

import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.ItemCardBinding
import black.bracken.neji.model.Item
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.viewbinding.BindableItem
import io.github.rosariopfernandes.firecoil.load

class ItemCardItem(
    val item: Item,
    private val imageRef: StorageReference?,
    private val onClick: (Item) -> Unit
) : BindableItem<ItemCardBinding>() {

    override fun getLayout() = R.layout.item_card

    override fun initializeViewBinding(view: View): ItemCardBinding {
        return ItemCardBinding.bind(view)
    }

    override fun bind(viewBinding: ItemCardBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { onClick(item) }
            viewBinding.textName.text = item.name

            imageRef?.also { ref ->
                viewBinding.imageItem.load(ref)
            }
        }
    }

}