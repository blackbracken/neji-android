package black.bracken.neji.ui.searchresult

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchResultFragmentBinding
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.searchedResults.collect { results ->
                // TODO: add result items
                println("sukoya shown results: ${results.joinToString()}")
            }
        }

        viewModel.addAllSearchedResults(args.searchedItems)
    }

}