package black.bracken.neji.ui.boxlist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import black.bracken.neji.R
import black.bracken.neji.databinding.BoxListFragmentBinding
import black.bracken.neji.ui.boxlist.item.BoxCardItem
import black.bracken.neji.util.Failure
import black.bracken.neji.util.ItemOffsetDecoration
import black.bracken.neji.util.Loading
import black.bracken.neji.util.Success
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoxListFragment : Fragment(R.layout.box_list_fragment) {

    private val viewModel by viewModels<BoxListViewModel>()
    private val binding by viewBinding(BoxListFragmentBinding::bind)
    private val args: BoxListFragmentArgs by navArgs()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.indicator.isIndeterminate = true
        viewModel.fetchBoxes(args.region)

        viewModel.boxes.observe(viewLifecycleOwner) { boxesResource ->
            when (boxesResource) {
                is Success -> {
                    boxesResource.value.forEach { box ->
                        adapter.add(BoxCardItem(requireContext(), box))
                    }
                    binding.indicator.isIndeterminate = false
                }
                is Failure -> {
                    Log.e("Neji", "failed to get box, error: ${boxesResource.error}")
                    binding.indicator.isIndeterminate = false
                }
                is Loading -> Unit /* do nothing */
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.apply {
            addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.recycler_padding))
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

}