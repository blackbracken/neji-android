package black.bracken.neji.ui.regionlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import black.bracken.neji.R
import black.bracken.neji.databinding.RegionListFragmentBinding
import black.bracken.neji.model.firebase.Region
import black.bracken.neji.ui.UserViewModel
import black.bracken.neji.ui.regionlist.item.RegionCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegionListFragment : Fragment(R.layout.region_list_fragment) {

    private val viewModel by viewModels<RegionListViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    private val binding by viewBinding(RegionListFragmentBinding::bind)

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.indicator.isIndeterminate = true
        userViewModel.firebaseApp.observe(viewLifecycleOwner) { firebaseApp ->
            if (firebaseApp != null) {
                onSignedIn()
            } else {
                findNavController().navigate(RegionListFragmentDirections.actionRegionListFragmentToSetupFragment())
            }

            binding.indicator.isIndeterminate = false
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

    private fun onSignedIn() {
        Snackbar.make(binding.root, R.string.snackbar_success_sign_in, Snackbar.LENGTH_SHORT).show()

        viewModel.regions.observe(viewLifecycleOwner) { regions ->
            adapter.clear()
            regions.forEach { region ->
                adapter.add(
                    RegionCardItem(
                        requireContext(),
                        region,
                        RegionListItemClickListener {
                            val action =
                                RegionListFragmentDirections.actionRegionListFragmentToBoxListFragment(
                                    it
                                )
                            findNavController().navigate(action)
                        })
                )
            }
        }

        binding.fabAddItem.setOnClickListener {
            findNavController().navigate(RegionListFragmentDirections.actionRegionListFragmentToAddItemFragment())
        }
    }

    interface RegionListItemClickListener {
        fun onClick(region: Region)

        companion object {
            operator fun invoke(lambdaListener: (Region) -> Unit): RegionListItemClickListener =
                object : RegionListItemClickListener {
                    override fun onClick(region: Region) {
                        lambdaListener(region)
                    }
                }
        }
    }

}