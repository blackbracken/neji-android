package black.bracken.neji.ui.addregion

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.AddRegionFragmentBinding
import black.bracken.neji.ext.closeSoftKeyboard
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddRegionFragment : Fragment(R.layout.add_region_fragment) {

    private val binding by viewBinding(AddRegionFragmentBinding::bind)
    private val viewModel: AddRegionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAdd.setOnClickListener {
            viewModel.addRegion(binding.editItemName.text?.toString()?.trim() ?: "")
            closeSoftKeyboard(view.rootView)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.registrationResult.collect { result ->
                if (result != null) {
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(view.rootView, "Failed to add the region", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}