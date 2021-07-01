package black.bracken.neji.ui.addcategory

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.R
import black.bracken.neji.databinding.AddCategoryFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryFragment : Fragment(R.layout.add_category_fragment) {

    private val binding: AddCategoryFragmentBinding by viewBinding(AddCategoryFragmentBinding::bind)
    private val viewModel: AddCategoryViewModel by viewModels()

}