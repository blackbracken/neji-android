package black.bracken.neji.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import black.bracken.neji.R
import black.bracken.neji.databinding.SetupFragmentBinding
import black.bracken.neji.model.FirebaseSignInResult
import com.google.android.material.snackbar.Snackbar
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

        viewModel.signInState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SetupViewModel.SignInState.Loading -> {
                    binding.buttonLogin.isEnabled = false
                }
                is SetupViewModel.SignInState.Done -> {
                    binding.buttonLogin.isEnabled = true

                    when (state.result) {
                        is FirebaseSignInResult.Success -> {
                            // TODO: navigate
                            Snackbar.make(binding.root, "navigate", Snackbar.LENGTH_SHORT).show()
                        }
                        is FirebaseSignInResult.InvalidValue -> {
                            Snackbar.make(
                                binding.root,
                                R.string.snackbar_invalid_value,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseSignInResult.MustNotBeBlank -> {
                            Snackbar.make(
                                binding.root,
                                R.string.snackbar_must_not_be_blank,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}