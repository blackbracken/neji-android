package black.bracken.neji.ui.setup

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import black.bracken.neji.R
import black.bracken.neji.databinding.SetupFragmentBinding
import black.bracken.neji.model.FirebaseSignInResult
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.setup_fragment) {

    private val viewModel by viewModels<SetupViewModel>()
    private val binding by viewBinding(SetupFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                            findNavController().navigate(
                                SetupFragmentDirections.actionSetupFragmentToTopFragment()
                            )
                        }
                        is FirebaseSignInResult.InvalidValue -> {
                            Snackbar.make(
                                binding.root,
                                R.string.snackbar_invalid_value,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseSignInResult.MustNotBeBlank -> {
                            // TODO: remove
                            findNavController().navigate(
                                SetupFragmentDirections.actionSetupFragmentToTopFragment()
                            )

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
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as AppCompatActivity)
            .findViewById<Toolbar>(R.id.toolbar).visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()

        (requireActivity() as AppCompatActivity)
            .findViewById<Toolbar>(R.id.toolbar).visibility = View.VISIBLE
    }

}