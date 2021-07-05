package black.bracken.neji.ui.addcategory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.AddCategoryFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddCategoryFragment : Fragment(R.layout.add_category_fragment) {

    private val binding: AddCategoryFragmentBinding by viewBinding(AddCategoryFragmentBinding::bind)
    private val viewModel: AddCategoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAdd.setOnClickListener {
            binding.buttonAdd.isEnabled = false

            val categoryName = binding.editCategoryName.text?.toString()
                ?: return@setOnClickListener

            viewModel.addCategory(categoryName)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addingResult.collect { result ->
                binding.buttonAdd.isEnabled = true

                if (result) {
                    findNavController().popBackStack()
                } else {
                    // TODO: error handling
                }
            }
        }
    }

}