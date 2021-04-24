package black.bracken.neji.ui.additem

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.AddItemFragmentBinding
import black.bracken.neji.ext.closeSoftKeyboard
import black.bracken.neji.util.ValidatedResult
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddItemFragment : Fragment(R.layout.add_item_fragment) {

    private val viewModel: AddItemViewModel by viewModels()
    private val binding: AddItemFragmentBinding by viewBinding()

    private val args: AddItemFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.itemCategories.collect { itemCategories ->
                if (itemCategories != null) {
                    binding.autoCompleteTextItemCategory.setAdapter(
                        ArrayAdapter(requireContext(), R.layout.list_item, itemCategories)
                    )
                } else {
                    Snackbar
                        .make(binding.root, "failed to get itemCategories", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.registrationResult.collect {
                closeSoftKeyboard(view)

                if (it != null) {
                    findNavController().popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.imageUri.collect { uri ->
                if (uri != null) {
                    binding.imageItem.load(uri)
                } else {
                    binding.imageItem.load(R.drawable.ic_baseline_memory_24)
                }
            }
        }

        binding.fabAddImage.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(2048)
                .maxResultSize(512, 512)
                .start { code, data ->
                    if (code != ImagePicker.RESULT_ERROR) {
                        viewModel.setImageUri(data?.data)
                    }
                }
        }

        binding.buttonAdd.setOnClickListener { tryToAddItem() }
    }

    private fun tryToAddItem() {
        val inputItemName = binding.inputItemName.apply { error = null }
        val inputItemAmount = binding.inputItemAmount.apply { error = null }
        val inputItemCategory = binding.inputItemCategory.apply { error = null }

        val nameResult = viewModel.validateName(
            requireContext(),
            binding.editItemName.text?.toString()
        )
        if (nameResult is ValidatedResult.Failure) {
            inputItemName.error = nameResult.error
        }

        val amountResult = viewModel.validateAmount(
            requireContext(),
            binding.editItemAmount.text?.toString()
        )
        if (amountResult is ValidatedResult.Failure) {
            inputItemAmount.error = amountResult.error
        }

        val itemCategoryResult = viewModel.validateItemCategoryText(
            binding.autoCompleteTextItemCategory.text?.toString()
        )
        if (itemCategoryResult is ValidatedResult.Failure) {
            inputItemCategory.error = itemCategoryResult.error
        }

        viewModel.addItem(
            context = requireContext(),
            name = nameResult.let { it as? ValidatedResult.Success }?.value
                ?: return,
            itemCategoryName = itemCategoryResult.let { it as? ValidatedResult.Success }?.value
                ?: return,
            amount = amountResult.let { it as? ValidatedResult.Success }?.value
                ?: return,
            comment = binding.editItemComment.text?.toString()
                ?: "",
            box = args.box
        )
    }

}