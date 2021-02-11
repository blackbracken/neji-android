package black.bracken.neji.ui.searchresult.item

import android.content.Context
import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchResultCardBinding
import black.bracken.neji.model.document.Item
import black.bracken.neji.ui.searchresult.SearchResultFragment
import com.xwray.groupie.viewbinding.BindableItem

class SearchResultCardItem(
    private val context: Context,
    val item: Item,
    private val listener: SearchResultFragment.SearchResultItemClickListener
) : BindableItem<SearchResultCardBinding>() {

    override fun getLayout() = R.layout.search_result_card

    override fun initializeViewBinding(view: View): SearchResultCardBinding {
        return SearchResultCardBinding.bind(view)
    }

    override fun bind(viewBinding: SearchResultCardBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { listener.onClick(item) }
            viewBinding.textName.text = item.name
        }
    }

}