package black.bracken.neji.ui.searchitem

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import arrow.core.Either
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchItemFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

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
            println("sukoya searching")
            with(binding) {
                viewModel.searchItems(
                    itemName = editElementName.text?.toString(),
                    itemType = autoCompleteTextElementItemType.text?.toString(),
                    regionName = autoCompleteTextElementRegion.text?.toString(),
                    boxName = autoCompleteTextElementBox.text?.toString()
                )
            }
        }

        viewModel.searchResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Either.Right -> Snackbar.make(
                    binding.root,
                    "results size is ${result.b.size}",
                    Snackbar.LENGTH_SHORT
                ).show()
                else -> Unit
            }
        }
    }

}