package black.bracken.neji.ui.searchitem

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchItemFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchItemFragment : Fragment(R.layout.search_item_fragment) {

    private val viewModel by viewModels<SearchItemViewModel>()
    private val binding by viewBinding(SearchItemFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.autoCompleteTextElementRegion.doOnTextChanged { _, _, _, _ ->
            binding.autoCompleteTextElementBox.text.clear()
        }

        binding.buttonSearch.setOnClickListener {
            with(binding) {
                viewModel.emitQuery(
                    itemName = editElementName.text?.toString()
                        ?: "", // TODO: handle if the string is blank or null
                    itemType = autoCompleteTextElementItemType.text?.toString()
                        ?.takeIf { it.isNotBlank() },
                    regionName = autoCompleteTextElementRegion.text?.toString()
                        ?.takeIf { it.isNotBlank() },
                    boxName = autoCompleteTextElementBox.text?.toString()
                        ?.takeIf { it.isNotBlank() }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchQuery.collect { query ->
                val action =
                    SearchItemFragmentDirections.actionSearchItemFragmentToSearchResultFragment(
                        query
                    )

                findNavController().navigate(action)
            }
        }
    }

}