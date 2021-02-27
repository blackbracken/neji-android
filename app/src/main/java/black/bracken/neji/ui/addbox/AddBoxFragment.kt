package black.bracken.neji.ui.addbox

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.AddBoxFragmentBinding
import black.bracken.neji.ui.regionlist.RegionListFragmentDirections
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBoxFragment : Fragment(R.layout.add_box_fragment) {

    private val binding: AddBoxFragmentBinding by viewBinding(AddBoxFragmentBinding::bind)
    private val viewModel: AddBoxViewModel by viewModels()

}