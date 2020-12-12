package black.bracken.neji.ui.additem

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.AddItemFragmentBinding
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemFragment : Fragment(R.layout.add_item_fragment) {

    private val binding by viewBinding(AddItemFragmentBinding::bind)

    private val viewModel by viewModels<AddItemViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.imageUri.observe(viewLifecycleOwner) { imageUri ->
            binding.imageItem.load(imageUri ?: "file:///android_asset/sample.png".toUri())
        }

        viewModel.itemTypes.observe(viewLifecycleOwner) { itemTypes ->
            binding.autoCompleteTextItemType.setAdapter(
                ArrayAdapter(requireContext(), R.layout.list_item, itemTypes)
            )
        }

        viewModel.regions.observe(viewLifecycleOwner) { regions ->
            binding.autoCompleteTextRegionOfBox.setAdapter(
                ArrayAdapter(requireContext(), R.layout.list_item, regions)
            )
        }

        viewModel.boxes.observe(viewLifecycleOwner) { boxes ->
            binding.inputBoxToSave.isEnabled = true
            binding.autoCompleteTextBoxToSave.text.clear()
            binding.autoCompleteTextBoxToSave.setAdapter(
                ArrayAdapter(requireContext(), R.layout.list_item, boxes)
            )
        }

        binding.autoCompleteTextRegionOfBox.doOnTextChanged { regionText, _, _, _ ->
            viewModel.fetchBoxesInRegion(
                viewModel.regions.value?.find { it.name == regionText.toString() }
                    ?: return@doOnTextChanged
            )
        }

        binding.fabAddImage.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(2048)
                .maxResultSize(512, 512)
                .start { _, data -> viewModel.setItemImage(data?.data) }
        }

        binding.buttonAdd.setOnClickListener { onPushButtonToAdd() }
    }

    private fun onPushButtonToAdd() {
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

        if (errors.isEmpty()) {
            viewModel.addItem(
                name = binding.editItemName.text.toString(),
                amount = binding.editItemAmount.text.toString().toInt(),
                itemType = binding.autoCompleteTextItemType.text.toString(),
                regionName = binding.autoCompleteTextRegionOfBox.text.toString(),
                boxName = binding.autoCompleteTextBoxToSave.text.toString(),
                comment = binding.editItemComment.text.toString().takeIf { it.isNotBlank() }
            )
            findNavController().popBackStack()
        } else {
            errors.forEach { error -> error() }
        }
    }

}