package black.bracken.neji.ui.categorylist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import black.bracken.neji.R
import black.bracken.neji.databinding.CategoryListFragmentBinding
import black.bracken.neji.ext.setOnSwipeItemToSideways
import black.bracken.neji.ext.viewcomponent.disableAndHide
import black.bracken.neji.ui.categorylist.item.CategoryCardItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class CategoryListFragment : Fragment(R.layout.category_list_fragment) {
    private val viewModel by viewModels<CategoryListViewModel>()
    private val binding: CategoryListFragmentBinding by viewBinding()

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.indicator.disableAndHide()

        binding.recycler.adapter = adapter
        binding.recycler.apply {
            setOnSwipeItemToSideways<CategoryCardItem> { categoryItem, pos ->
                onDeleteCategory(categoryItem, pos)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categories
                .filterNotNull()
                .map { categories ->
                    categories.map {
                        CategoryCardItem(it, onClick = { /* do nothing */ })
                    }
                }
                .collect { categoryCardItems ->
                    adapter.clear()

                    categoryCardItems.forEach { cardItem ->
                        adapter.add(cardItem)
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.category_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.add_category -> {
                Snackbar.make(requireView(), "add category", Snackbar.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun onDeleteCategory(categoryItem: CategoryCardItem, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_alert_on_deleting_category)
            .setCancelable(true)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                adapter.removeGroupAtAdapterPosition(position)
                viewModel.deleteCategory(categoryItem.category)
            }
            .show()
    }

}