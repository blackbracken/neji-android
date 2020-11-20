package black.bracken.neji.ui.addparts

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import black.bracken.neji.R
import black.bracken.neji.databinding.AddPartsFragmentBinding
import coil.load
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPartsFragment : Fragment(R.layout.add_parts_fragment) {

    private val binding by viewBinding(AddPartsFragmentBinding::bind)

    private val viewModel by viewModels<AddPartsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.partsTypes.observe(viewLifecycleOwner) { partsTypes ->
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, partsTypes)
            binding.autoCompleteTextPartsType.setAdapter(adapter)
        }

        binding.imageParts.load("file:///android_asset/sample.png")
    }

}