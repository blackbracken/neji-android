package black.bracken.neji.ui.addparts

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import black.bracken.neji.R
import black.bracken.neji.databinding.AddPartsFragmentBinding
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPartsFragment : Fragment(R.layout.add_parts_fragment) {

    private val binding by viewBinding(AddPartsFragmentBinding::bind)

    private val viewModel by viewModels<AddPartsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.imageParts.load("file:///android_asset/sample.png") {
            crossfade(true)
        }

        binding.buttonAdd.setOnClickListener {
            val inputPartsName = binding.inputPartsName.apply { error = null }
            val inputPartsAmount = binding.inputPartsAmount.apply { error = null }
            val inputPartsType = binding.inputPartsType.apply { error = null }
            val inputRegionOfBox = binding.inputRegionOfBox.apply { error = null }
            val inputBoxToSave = binding.inputBoxToSave.apply { error = null }

            val errors = mutableListOf<() -> Unit>()
                .apply {
                    if (binding.editPartsName.text.isNullOrBlank())
                        add { inputPartsName.error = "未入力です" }

                    if (binding.editPartsAmount.text?.toString()
                            ?.toIntOrNull()
                            ?.takeIf { it >= 0 } == null
                    )
                        add { inputPartsAmount.error = "0以上の数値にしてください" }

                    if (binding.autoCompleteTextPartsType.text?.toString().isNullOrBlank())
                        add { inputPartsType.error = "指定してください" }

                    if (binding.autoCompleteTextRegionOfBox.text?.toString().isNullOrBlank())
                        add { inputRegionOfBox.error = "指定してください" }

                    if (binding.autoCompleteTextBoxToSave.text?.toString().isNullOrBlank())
                        add { inputBoxToSave.error = "指定してください" }
                }
                .toList()

            if (errors.isEmpty()) {
                Snackbar.make(binding.root, "追加処理", Snackbar.LENGTH_SHORT).show()
            } else {
                errors.forEach { error -> error() }
            }
        }
    }

}