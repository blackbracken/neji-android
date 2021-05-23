package black.bracken.neji.ui.categorylist.item

import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.CategoryListCardBinding
import black.bracken.neji.model.ItemCategory
import com.xwray.groupie.viewbinding.BindableItem

class CategoryCardItem(
    private val category: ItemCategory,
    private val onClick: (ItemCategory) -> Unit
) : BindableItem<CategoryListCardBinding>() {

    override fun getLayout() = R.layout.category_list_card

    override fun initializeViewBinding(view: View): CategoryListCardBinding {
        return CategoryListCardBinding.bind(view)
    }

    override fun bind(viewBinding: CategoryListCardBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { onClick(category) }
            textCategory.text = category.name
        }
    }

}