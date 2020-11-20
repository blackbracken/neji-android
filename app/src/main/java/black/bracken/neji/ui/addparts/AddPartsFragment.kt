package black.bracken.neji.ui.addparts

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import black.bracken.neji.R
import black.bracken.neji.databinding.AddPartsFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPartsFragment : Fragment(R.layout.add_parts_fragment) {

    private val binding by viewBinding(AddPartsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = (1..30).map { "ねじ ${it}型" } // TODO: replace correct parts
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.autoCompleteTextPartsType.setAdapter(adapter)
    }

}