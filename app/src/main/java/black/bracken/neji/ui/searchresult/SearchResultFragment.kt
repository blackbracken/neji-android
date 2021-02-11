package black.bracken.neji.ui.searchresult

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchResultFragmentBinding
import black.bracken.neji.model.document.Item
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.search_result_fragment) {

    private val viewModel by viewModels<SearchResultViewModel>()
    private val binding by viewBinding(SearchResultFragmentBinding::bind)
    private val args: SearchResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchedResults.collect { results ->
                // TODO: add result items
            }
        }

        viewModel.addAllSearchedResults(args.searchedItems)
    }

    interface SearchResultItemClickListener {
        fun onClick(item: Item)

        companion object {
            operator fun invoke(lambdaListener: (Item) -> Unit): SearchResultItemClickListener =
                object : SearchResultItemClickListener {
                    override fun onClick(item: Item) {
                        lambdaListener(item)
                    }
                }
        }
    }

}