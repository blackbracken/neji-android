package black.bracken.neji.ui.top

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.R
import black.bracken.neji.databinding.TopFragmentBinding
import black.bracken.neji.ui.top.item.TopCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopFragment : Fragment(R.layout.top_fragment) {

    private val viewModel by viewModels<TopViewModel>()
    private val binding by viewBinding(TopFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupAdapter<GroupieViewHolder>()
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            ItemOffsetDecoration(
                requireContext(),
                R.dimen.recycler_padding
            )
        )

        // TODO: remove
        repeat(20) { adapter.add(TopCardItem()) }
    }

}