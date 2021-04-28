package black.bracken.neji.ui.regionlist

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import black.bracken.neji.R
import black.bracken.neji.databinding.RegionListFragmentBinding
import black.bracken.neji.ext.setOnSwipeItemToSideways
import black.bracken.neji.ext.viewcomponent.disableAndHide
import black.bracken.neji.model.Region
import black.bracken.neji.ui.UserViewModel
import black.bracken.neji.ui.regionlist.item.RegionCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegionListFragment : Fragment(R.layout.region_list_fragment) {

    private val viewModel by viewModels<RegionListViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    private val binding by viewBinding(RegionListFragmentBinding::bind)

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.indicator.isIndeterminate = true
        userViewModel.firebaseApp.observe(viewLifecycleOwner) { firebaseApp ->
            if (firebaseApp != null) {
                onSuccessInSigningIn()
            } else {
                findNavController().navigate(RegionListFragmentDirections.actionRegionListFragmentToSetupFragment())
            }
        }

        binding.recycler.adapter = adapter
        binding.recycler.apply {
            addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.recycler_padding))
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )

            setOnSwipeItemToSideways<RegionCardItem> { item, pos ->
                onDeleteRegion(item.region, pos)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.region_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.add_region -> {
                findNavController().navigate(RegionListFragmentDirections.actionRegionListFragmentToAddRegionFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun onSuccessInSigningIn() {
        Snackbar.make(binding.root, R.string.snackbar_success_sign_in, Snackbar.LENGTH_SHORT).show()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.regions.collect { regions ->
                adapter.clear()
                regions
                    ?.map { region ->
                        val listener = RegionListItemClickListener { newRegion ->
                            val action = RegionListFragmentDirections
                                .actionRegionListFragmentToBoxListFragment(newRegion)

                            findNavController().navigate(action)
                        }

                        RegionCardItem(requireContext(), region, listener)
                    }
                    ?.forEach { regionCard -> adapter.add(regionCard) }
                    ?: run {
                        Snackbar
                            .make(binding.root, "failed to get regions", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .show()
                    }

                binding.indicator.disableAndHide()
            }
        }

        binding.fabEditItem.setOnClickListener {
            findNavController().navigate(RegionListFragmentDirections.actionRegionListFragmentToSearchItemFragment())
        }
    }

    private fun onDeleteRegion(region: Region, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_alert_on_deleting_region)
            .setCancelable(true)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                adapter.removeGroupAtAdapterPosition(position)
                viewModel.deleteRegion(region)
            }
            .show()
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