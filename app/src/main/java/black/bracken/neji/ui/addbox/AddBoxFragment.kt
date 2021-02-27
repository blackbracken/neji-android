package black.bracken.neji.ui.addbox

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.R
import black.bracken.neji.databinding.AddBoxFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBoxFragment : Fragment(R.layout.add_box_fragment) {

    private val binding: AddBoxFragmentBinding by viewBinding(AddBoxFragmentBinding::bind)
    private val viewModel: AddBoxViewModel by viewModels()

}