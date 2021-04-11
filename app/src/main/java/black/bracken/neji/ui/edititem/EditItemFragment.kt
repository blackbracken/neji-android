package black.bracken.neji.ui.edititem

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.EditItemFragmentBinding
import black.bracken.neji.util.Failure
import black.bracken.neji.util.Loading
import black.bracken.neji.util.Success
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosariopfernandes.firecoil.load
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EditItemFragment : Fragment(R.layout.edit_item_fragment) {

    private val binding by viewBinding(EditItemFragmentBinding::bind)
    private val viewModel by viewModels<EditItemViewModel>()
    private val args: EditItemFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.editResult.collect { result ->
                if (result != null) {
                    val action =
                        EditItemFragmentDirections.actionEditItemFragmentToItemInfoFragment(result)

                    findNavController().navigate(action)
                } else {
                    Snackbar
                        .make(binding.root, "failed to edit", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }

        viewModel.imageUri.observe(viewLifecycleOwner) { imageUri ->
            if (imageUri != null) {
                binding.imageItem.load(imageUri)
            } else {
                binding.imageItem.load(R.drawable.ic_baseline_memory_24)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.itemTypes.collect { itemTypes ->
                if (itemTypes != null) {
                    binding.autoCompleteTextItemType.setAdapter(
                        ArrayAdapter(requireContext(), R.layout.list_item, itemTypes)
                    )
                } else {
                    Snackbar
                        .make(binding.root, "failed to get itemTypes", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.regions.collect { regions ->
                if (regions != null) {
                    binding.autoCompleteTextRegionOfBox.setAdapter(
                        ArrayAdapter(requireContext(), R.layout.list_item, regions)
                    )
                } else {
                    Snackbar
                        .make(binding.root, "failed to get regions", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.boxes.collect { result ->
                when (result) {
                    is Loading -> {
                        /* do nothing */
                    }
                    is Success -> {
                        val boxes = result.value

                        binding.inputBoxToSave.isEnabled = true
                        binding.autoCompleteTextBoxToSave.text.clear()
                        binding.autoCompleteTextBoxToSave.setAdapter(
                            ArrayAdapter(requireContext(), R.layout.list_item, boxes)
                        )
                    }
                    is Failure -> {
                        Snackbar
                            .make(binding.root, "failed to get boxes", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .show()
                    }
                }
            }
        }

        binding.autoCompleteTextRegionOfBox.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateBoxesByRegionIndex(position)
        }

        binding.fabAddImage.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(2048)
                .maxResultSize(512, 512)
                .start { _, data -> viewModel.setItemImage(data?.data) }
        }

        binding.buttonAdd.setOnClickListener {
            onPushButtonToAdd()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(1000L) // TODO: set values after collecting valid data from some flows
            setOriginValues()
        }
    }

    private fun setOriginValues() {
        val origin = args.item

        binding.editItemName.setText(origin.name)
        binding.editItemAmount.setText(origin.amount.toString())
        binding.autoCompleteTextItemType.setText(origin.itemType)

        binding.autoCompleteTextRegionOfBox.setText(origin.box.region.name)
        viewModel.updateBoxesByRegionName(origin.box.region.name)

        binding.autoCompleteTextBoxToSave.setText(origin.box.name)
        origin.imageReference?.also { binding.imageItem.load(it) }
    }

    private fun onPushButtonToAdd() {
        // TODO: validate in viewModel
        val inputItemName = binding.inputItemName.apply { error = null }
        val inputItemAmount = binding.inputItemAmount.apply { error = null }
        val inputItemType = binding.inputItemType.apply { error = null }
        val inputRegionOfBox = binding.inputRegionOfBox.apply { error = null }
        val inputBoxToSave = binding.inputBoxToSave.apply { error = null }

        val errors: List<() -> Unit> = mutableListOf<() -> Unit>()
            .apply {
                if (binding.editItemName.text.isNullOrBlank())
                    add { inputItemName.error = getString(R.string.error_must_not_be_blank) }

                if (binding.editItemAmount.text?.toString()
                        ?.toIntOrNull()
                        ?.takeIf { it >= 0 } == null
                ) {
                    add {
                        inputItemAmount.error =
                            getString(R.string.error_must_be_integer_and_at_least_zero)
                    }
                }

                if (binding.autoCompleteTextItemType.text?.toString().isNullOrBlank())
                    add { inputItemType.error = getString(R.string.error_must_not_be_blank) }

                if (binding.autoCompleteTextRegionOfBox.text?.toString().isNullOrBlank())
                    add { inputRegionOfBox.error = getString(R.string.error_must_not_be_blank) }

                if (binding.autoCompleteTextBoxToSave.text?.toString().isNullOrBlank())
                    add { inputBoxToSave.error = getString(R.string.error_must_not_be_blank) }
            }
            .toList()

        if (errors.none()) {
            viewModel.editItem(
                context = requireContext(),
                source = args.item,
                name = binding.editItemName.text.toString(),
                amount = binding.editItemAmount.text.toString().toInt(),
                itemTypeText = binding.autoCompleteTextItemType.text.toString(),
                boxText = binding.autoCompleteTextBoxToSave.text.toString(),
                comment = binding.editItemComment.text.toString().takeIf { it.isNotBlank() }
            )
        } else {
            errors.forEach { error -> error() }
        }
    }

}