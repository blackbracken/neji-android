package black.bracken.neji.ui.searchitem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchItemFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchItemFragment : Fragment(R.layout.search_item_fragment) {

    private val viewModel by viewModels<SearchItemViewModel>()
    private val binding by viewBinding(SearchItemFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearch.setOnClickListener {
            with(binding) {
                viewModel.searchItems(
                    itemName = editElementName.text?.toString(),
                    itemType = autoCompleteTextElementItemType.text?.toString(),
                    regionName = autoCompleteTextElementRegion.text?.toString(),
                    boxName = autoCompleteTextElementBox.text?.toString()
                )
            }
            // TODO: implement
            findNavController().navigate(SearchItemFragmentDirections.actionSearchItemFragmentToRegionListFragment())
        }
    }

}