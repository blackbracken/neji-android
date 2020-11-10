package black.bracken.neji.ui.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.R
import black.bracken.neji.databinding.TopFragmentBinding
import black.bracken.neji.ui.top.item.TopCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopFragment : Fragment() {

    private val viewModel by viewModels<TopViewModel>()

    private var _binding: TopFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = TopFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

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