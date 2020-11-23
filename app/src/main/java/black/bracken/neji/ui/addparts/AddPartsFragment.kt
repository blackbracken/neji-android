package black.bracken.neji.ui.addparts

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
import black.bracken.neji.databinding.AddPartsFragmentBinding
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPartsFragment : Fragment(R.layout.add_parts_fragment) {

    private val binding by viewBinding(AddPartsFragmentBinding::bind)

    private val viewModel by viewModels<AddPartsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.imageUri.observe(viewLifecycleOwner) { imageUri ->
            binding.imageParts.load(imageUri ?: "file:///android_asset/sample.png".toUri())
        }

        viewModel.partsTypes.observe(viewLifecycleOwner) { partsTypes ->
            binding.autoCompleteTextPartsType.setAdapter(
                ArrayAdapter(requireContext(), R.layout.list_item, partsTypes)
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
                .start { _, data -> viewModel.setPartsImage(data?.data) }
        }

        binding.buttonAdd.setOnClickListener { onPushButtonToAdd() }
    }

    private fun onPushButtonToAdd() {
        val inputPartsName = binding.inputPartsName.apply { error = null }
        val inputPartsAmount = binding.inputPartsAmount.apply { error = null }
        val inputPartsType = binding.inputPartsType.apply { error = null }
        val inputRegionOfBox = binding.inputRegionOfBox.apply { error = null }
        val inputBoxToSave = binding.inputBoxToSave.apply { error = null }

        val errors = mutableListOf<() -> Unit>()
            .apply {
                if (binding.editPartsName.text.isNullOrBlank())
                    add { inputPartsName.error = getString(R.string.error_must_not_be_blank) }

                if (binding.editPartsAmount.text?.toString()
                        ?.toIntOrNull()
                        ?.takeIf { it >= 0 } == null
                )
                    add {
                        inputPartsAmount.error =
                            getString(R.string.error_must_be_integer_and_at_least_zero)
                    }

                if (binding.autoCompleteTextPartsType.text?.toString().isNullOrBlank())
                    add { inputPartsType.error = getString(R.string.error_must_not_be_blank) }

                if (binding.autoCompleteTextRegionOfBox.text?.toString().isNullOrBlank())
                    add { inputRegionOfBox.error = getString(R.string.error_must_not_be_blank) }

                if (binding.autoCompleteTextBoxToSave.text?.toString().isNullOrBlank())
                    add { inputBoxToSave.error = getString(R.string.error_must_not_be_blank) }
            }
            .toList()

        if (errors.isEmpty()) {
            viewModel.addParts(
                name = binding.editPartsName.text.toString(),
                amount = binding.editPartsAmount.text.toString().toInt(),
                partsType = binding.autoCompleteTextPartsType.text.toString(),
                regionName = binding.autoCompleteTextRegionOfBox.text.toString(),
                boxName = binding.autoCompleteTextBoxToSave.text.toString(),
                comment = binding.editPartsComment.text.toString().takeIf { it.isNotBlank() }
            )
            findNavController().popBackStack()
        } else {
            errors.forEach { error -> error() }
        }
    }

}