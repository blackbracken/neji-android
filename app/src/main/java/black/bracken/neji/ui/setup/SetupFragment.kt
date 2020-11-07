package black.bracken.neji.ui.setup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
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

        binding.buttonLogin.setOnClickListener {
            val projectId = binding.editFirebaseProjectId.text?.toString() ?: ""
            val apiKey = binding.editFirebaseApiKey.text?.toString() ?: ""
            val appId = binding.editFirebaseAppId.text?.toString() ?: ""
            val email = binding.editLoginEmail.text?.toString() ?: ""
            val password = binding.editLoginPassword.text?.toString() ?: ""

            viewModel.verifyFirebase(projectId, apiKey, appId, email, password)
        }

        viewModel.verifyResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SetupViewModel.VerifyResult.Success -> Log.i("aaa", "success")
                is SetupViewModel.VerifyResult.Failure -> Log.i("aaa", "failed, ${result.message}")
                is SetupViewModel.VerifyResult.Timeout -> Log.i("aaa", "timeout!")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}