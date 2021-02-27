package black.bracken.neji.ui.addregion

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.R
import black.bracken.neji.databinding.AddRegionFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRegionFragment : Fragment(R.layout.add_region_fragment) {

    private val binding by viewBinding(AddRegionFragmentBinding::bind)
    private val viewModel: AddRegionViewModel by viewModels()

}