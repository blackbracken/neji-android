package black.bracken.neji.ui.setup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.databinding.SetupFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private val viewModel by viewModels<SetupViewModel>()

    private var _binding: SetupFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = SetupFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}