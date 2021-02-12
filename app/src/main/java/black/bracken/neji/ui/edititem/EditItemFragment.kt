package black.bracken.neji.ui.edititem

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import black.bracken.neji.R
import black.bracken.neji.databinding.EditItemFragmentBinding
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditItemFragment : Fragment(R.layout.edit_item_fragment) {

    private val binding by viewBinding(EditItemFragmentBinding::bind)

    private val viewModel by viewModels<EditItemViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.imageUri.observe(viewLifecycleOwner) { imageUri ->
            if (imageUri != null) {
                binding.imageItem.load(imageUri)
            } else {
                binding.imageItem.load(R.drawable.ic_baseline_memory_24)
            }

        }

        viewModel.itemTypesResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Either.Right -> {
                    binding.autoCompleteTextItemType.setAdapter(
                        ArrayAdapter(requireContext(), R.layout.list_item, result.b)
                    )
                }
                is Either.Left -> {
                    Snackbar
                        .make(binding.root, result.a.toString(), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }

        viewModel.regionsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Either.Right -> {
                    binding.autoCompleteTextRegionOfBox.setAdapter(
                        ArrayAdapter(requireContext(), R.layout.list_item, result.b.map { it.name })
                    )
                }
                is Either.Left -> {
                    Snackbar
                        .make(binding.root, result.a.toString(), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }


        viewModel.boxesResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Either.Right -> {
                    binding.inputBoxToSave.isEnabled = true
                    binding.autoCompleteTextBoxToSave.text.clear()
                    binding.autoCompleteTextBoxToSave.setAdapter(
                        ArrayAdapter(requireContext(), R.layout.list_item, result.b.map { it.name })
                    )
                }
                is Either.Left -> {
                    Snackbar
                        .make(binding.root, result.a.toString(), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }

        binding.autoCompleteTextRegionOfBox.doOnTextChanged { regionText, _, _, _ ->
            viewModel.subscribeBoxesInRegion(
                viewModel.regionsResult.value
                    ?.orNull()
                    ?.find { it.name == regionText.toString() }
                    ?.id
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

        if (errors.none()) {
            viewModel.addItem(
                name = binding.editItemName.text.toString(),
                amount = binding.editItemAmount.text.toString().toInt(),
                itemType = binding.autoCompleteTextItemType.text.toString(),
                boxName = binding.autoCompleteTextBoxToSave.text.toString(),
                comment = binding.editItemComment.text.toString().takeIf { it.isNotBlank() }
            )
            findNavController().popBackStack()
        } else {
            errors.forEach { error -> error() }
        }
    }

}