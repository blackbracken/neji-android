package black.bracken.neji.ui.top

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.TopFragmentBinding
import black.bracken.neji.ui.UserViewModel
import black.bracken.neji.ui.top.item.TopCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopFragment : Fragment(R.layout.top_fragment) {

    private val viewModel by viewModels<TopViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    private val binding by viewBinding(TopFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.indicator.isIndeterminate = true
        userViewModel.firebaseApp.observe(viewLifecycleOwner) { firebaseApp ->
            if (firebaseApp != null) {
                Snackbar.make(
                    binding.root,
                    "HELLLLLLLLLOOOOOOOOOOOO!!!!", // TODO: set correct text
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(TopFragmentDirections.actionTopFragmentToSetupFragment())
            }

            binding.indicator.isIndeterminate = false
        }

        val adapter = GroupAdapter<GroupieViewHolder>()
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            ItemOffsetDecoration(
                requireContext(),
                R.dimen.recycler_padding
            )
        )

        binding.fab.setOnClickListener {
            findNavController().navigate(TopFragmentDirections.actionTopFragmentToAddPartsFragment())
        }

        // TODO: remove
        repeat(20) { adapter.add(TopCardItem()) }
    }

}