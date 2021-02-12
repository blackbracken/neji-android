package black.bracken.neji.ui.searchresult

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import black.bracken.neji.R
import black.bracken.neji.databinding.SearchResultFragmentBinding
import black.bracken.neji.model.document.Item
import black.bracken.neji.ui.searchresult.item.SearchResultCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import black.bracken.neji.util.firebaseStorage
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.search_result_fragment) {

    private val viewModel by viewModels<SearchResultViewModel>()
    private val binding by viewBinding(SearchResultFragmentBinding::bind)
    private val args: SearchResultFragmentArgs by navArgs()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = adapter
        binding.recycler.apply {
            addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.recycler_padding))
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchedResults.collect { results ->
                results
                    .map { item ->
                        val listener = SearchResultItemClickListener { newItem ->
                            // TODO: navigate to view for showing information of item
                        }

                        SearchResultCardItem(
                            requireContext(),
                            item,
                            item.imageUrl?.let { firebaseStorage.getReference(it) },
                            listener
                        )
                    }
                    .forEach { searchedItemCard -> adapter.add(searchedItemCard) }
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