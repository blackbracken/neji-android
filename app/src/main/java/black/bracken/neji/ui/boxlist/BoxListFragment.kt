package black.bracken.neji.ui.boxlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import black.bracken.neji.R
import black.bracken.neji.databinding.BoxListFragmentBinding
import black.bracken.neji.ext.setOnSwipeItemToSideways
import black.bracken.neji.ext.viewcomponent.disableAndHide
import black.bracken.neji.ui.boxlist.item.BoxCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BoxListFragment : Fragment(R.layout.box_list_fragment) {

    private val viewModel by viewModels<BoxListViewModel>()
    private val binding by viewBinding(BoxListFragmentBinding::bind)
    private val args: BoxListFragmentArgs by navArgs()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.indicator.isIndeterminate = true
        viewModel.fetchBoxes(args.region)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.boxes.collect { boxes ->
                adapter.clear()
                boxes.forEach { box ->
                    val card = BoxCardItem(
                        requireContext(),
                        box,
                        BoxListViewModel.BoxListItemClickListener { newBox ->
                            val action = BoxListFragmentDirections
                                .actionBoxListFragmentToItemListFragment(newBox)

                            findNavController().navigate(action)
                        }
                    )

                    adapter.add(card)
                }

                binding.indicator.disableAndHide()
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

            setOnSwipeItemToSideways<BoxCardItem> { cardItem, pos ->
                onDeleteBox(cardItem, pos)
            }
        }

        binding.fabSearchItem.setOnClickListener {
            findNavController().navigate(BoxListFragmentDirections.actionBoxListFragmentToSearchItemFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.box_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.add_box -> {
                val action =
                    BoxListFragmentDirections.actionBoxListFragmentToAddBoxFragment(
                        args.region,
                        null
                    )
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun onDeleteBox(cardItem: BoxCardItem, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_alert_on_deleting_box)
            .setCancelable(true)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                adapter.removeGroupAtAdapterPosition(position)
                viewModel.deleteBox(cardItem.box)
            }
            .show()
    }

}