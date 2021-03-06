package black.bracken.neji.ui.regionlist.item

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import black.bracken.neji.R
import black.bracken.neji.databinding.RegionListCardBinding
import black.bracken.neji.model.Region
import black.bracken.neji.ui.regionlist.RegionListFragment
import com.xwray.groupie.viewbinding.BindableItem

class RegionCardItem(
    private val context: Context,
    val region: Region,
    private val listener: RegionListFragment.RegionListItemClickListener
) : BindableItem<RegionListCardBinding>() {

    override fun getLayout() = R.layout.region_list_card

    override fun initializeViewBinding(view: View): RegionListCardBinding {
        return RegionListCardBinding.bind(view)
    }

    override fun bind(viewBinding: RegionListCardBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener { listener.onClick(region) }
            textName.text = region.name
            textAmount.text =
                context.getString(R.string.region_list_card_box_amount, region.boxAmount)
        }
    }

    override fun getSwipeDirs(): Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

}