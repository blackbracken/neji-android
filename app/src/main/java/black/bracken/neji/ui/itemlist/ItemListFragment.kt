package black.bracken.neji.ui.itemlist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import black.bracken.neji.R
import black.bracken.neji.databinding.ItemListFragmentBinding
import black.bracken.neji.ext.setOnSwipeItemToSideways
import black.bracken.neji.ext.viewcomponent.disableAndHide
import black.bracken.neji.model.Item
import black.bracken.neji.ui.itemlist.item.ItemCardItem
import black.bracken.neji.util.ItemOffsetDecoration
import black.bracken.neji.util.createQrCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ItemListFragment : Fragment(R.layout.item_list_fragment) {

    private val viewModel: ItemListViewModel by viewModels()
    private val binding by viewBinding(ItemListFragmentBinding::bind)
    private val args: ItemListFragmentArgs by navArgs()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.indicator.isIndeterminate = true

        binding.recycler.adapter = adapter
        binding.recycler.apply {
            addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen.recycler_padding))
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )

            setOnSwipeItemToSideways<ItemCardItem> { cardItem, pos ->
                onDeleteItem(cardItem.item, pos)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.items.collect { items ->
                binding.indicator.disableAndHide()
                adapter.clear()

                items
                    ?.map { item ->
                        ItemCardItem(item, item.imageReference) { newItem ->
                            val action = ItemListFragmentDirections
                                .actionItemListFragmentToItemInfoFragment(newItem)

                            findNavController().navigate(action)
                        }
                    }
                    ?.forEach { itemCard -> adapter.add(itemCard) }
                    ?: run {
                        Snackbar.make(view.rootView, "Failed to get items", Snackbar.LENGTH_SHORT)
                            .show()
                    }
            }
        }
        viewModel.fetchItems(args.box)

        binding.fabEditItem.setOnClickListener {
            findNavController().navigate(ItemListFragmentDirections.actionItemListFragmentToSearchItemFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.item_list_menu, menu)

        if (args.box.qrCodeText == null) {
            (0 until menu.size())
                .map { idx -> menu.getItem(idx) }
                .filter { item -> item.itemId in setOf(R.id.show_qr_code, R.id.find_qr_code) }
                .forEach { item -> menu.removeItem(item.itemId) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.add_item -> {
                val action =
                    ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(args.box)

                findNavController().navigate(action)
                true
            }
            R.id.show_qr_code -> {
                args.box.qrCodeText
                    ?.also { qrCodeValue -> showQrCode(qrCodeValue) }
                    ?: run {
                        Snackbar
                            .make(requireView(), "No qrcode found", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                true
            }
            R.id.find_qr_code -> {
                args.box
                    .takeIf { it.qrCodeText != null }
                    ?.also {
                        val action =
                            ItemListFragmentDirections.actionItemListFragmentToQrSearchFragment(
                                args.box
                            )
                        findNavController().navigate(action)
                    }
                    ?: run {
                        Snackbar
                            .make(requireView(), "No qrcode found", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun onDeleteItem(item: Item, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_alert_on_deleting_item)
            .setCancelable(true)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                adapter.removeGroupAtAdapterPosition(position)
                viewModel.deleteItem(item)
            }
            .show()
    }

    private fun showQrCode(qrCodeValue: String) {
        val imageView = ImageView(requireContext())
            .apply {
                setImageBitmap(createQrCode(requireContext(), qrCodeValue))
            }

        Dialog(requireContext())
            .apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setOnDismissListener { /* do nothing */ }
                addContentView(
                    imageView, RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
            .show()
    }

}