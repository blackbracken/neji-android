package black.bracken.neji.ui.addbox

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.AddBoxFragmentBinding
import black.bracken.neji.ext.closeSoftKeyboard
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddBoxFragment : Fragment(R.layout.add_box_fragment) {

    private val binding: AddBoxFragmentBinding by viewBinding(AddBoxFragmentBinding::bind)
    private val viewModel: AddBoxViewModel by viewModels()

    private val args: AddBoxFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonScan.setOnClickListener {
            findNavController().navigate(AddBoxFragmentDirections.actionAddBoxFragmentToScanQrCodeFragment())
        }

        binding.buttonAdd.setOnClickListener {
            viewModel.addBox(
                name = binding.editItemName.text?.toString()?.trim() ?: "",
                region = args.region
            )
            closeSoftKeyboard(view)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.registrationResult.collect { result ->
                if (result != null) {
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(view.rootView, "Failed to add the box", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}