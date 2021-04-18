package black.bracken.neji.ui.searchitem

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchItemFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchItemFragment : Fragment(R.layout.search_item_fragment) {

    private val viewModel by viewModels<SearchItemViewModel>()
    private val binding by viewBinding(SearchItemFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchQuery.collect { query ->
                val action =
                    SearchItemFragmentDirections.actionSearchItemFragmentToSearchResultFragment(
                        query
                    )

                // TODO: show error if fails
                findNavController().navigate(action)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.itemTypes.collect { itemTypes ->
                if (itemTypes == null) {
                    Snackbar
                        .make(
                            requireView().rootView,
                            "Failed to get itemTypes",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                    return@collect
                }

                binding.autoCompleteTextElementItemType.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        itemTypes
                    )
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.regions.collect { regions ->
                if (regions == null) {
                    Snackbar
                        .make(
                            requireView().rootView,
                            "Failed to get regions",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                    return@collect
                }

                binding.autoCompleteTextElementRegion.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        regions
                    )
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.boxesAtSelectedRegion.collect { boxes ->
                if (boxes == null) {
                    Snackbar
                        .make(
                            requireView().rootView,
                            "Failed to get boxes",
                            Snackbar.LENGTH_SHORT
                        )
                        .show()
                    return@collect
                }

                binding.autoCompleteTextElementBox.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        boxes
                    )
                )
            }
        }

        binding.autoCompleteTextElementRegion.setOnItemClickListener { _, _, position, _ ->
            binding.autoCompleteTextElementBox.text.clear()
            viewModel.selectRegion(position)
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
    }

}