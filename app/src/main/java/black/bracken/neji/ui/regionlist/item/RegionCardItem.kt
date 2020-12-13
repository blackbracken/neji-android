package black.bracken.neji.ui.regionlist.item

import android.content.Context
import android.view.View
import black.bracken.neji.R
import black.bracken.neji.databinding.RegionListCardBinding
import black.bracken.neji.model.firebase.Region
import com.xwray.groupie.viewbinding.BindableItem

class RegionCardItem(
    private val context: Context,
    private val region: Region
) : BindableItem<RegionListCardBinding>() {

    override fun getLayout() = R.layout.region_list_card

    override fun initializeViewBinding(view: View): RegionListCardBinding {
        return RegionListCardBinding.bind(view)
    }

    override fun bind(viewBinding: RegionListCardBinding, position: Int) {
        with(viewBinding) {
            textName.text = context.getString(R.string.region_list_card_region_name, region.name)
            textAmount.text =
                context.getString(R.string.region_list_card_box_amount, region.boxIdSet().size)
        }
    }

}