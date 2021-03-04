package black.bracken.neji.ui.iteminfo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.ItemInfoFragmentBinding
import coil.load
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosariopfernandes.firecoil.load

@AndroidEntryPoint
class ItemInfoFragment : Fragment(R.layout.item_info_fragment) {

    private val viewModel: ItemInfoViewModel by viewModels()
    private val binding: ItemInfoFragmentBinding by viewBinding(ItemInfoFragmentBinding::bind)
    private val args: ItemInfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel.item.observe(viewLifecycleOwner) { item ->
            with(binding) {
                if (item.imageReference != null) {
                    imageItem.load(item.imageReference)
                } else {
                    imageItem.load(R.drawable.ic_baseline_memory_24)
                }

                textName.text = item.name
                textType.text = Math.random().toString()
                // TODO: i18n
                textPath.text = "${item.box.region.name} > ${item.box.name}"
                pickerAmount.progress = item.amount
                textComment.text = item.comment
            }
        }

        binding.buttonDetermineAmount.setOnClickListener {
            viewModel.setAmount(binding.pickerAmount.progress)
        }

        viewModel.addItem(args.item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.item_info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.edit_item -> {
                val action =
                    ItemInfoFragmentDirections.actionItemInfoFragmentToEditItemFragment()

                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


}