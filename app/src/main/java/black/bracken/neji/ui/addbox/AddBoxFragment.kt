package black.bracken.neji.ui.addbox

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.AddBoxFragmentBinding
import black.bracken.neji.ext.closeSoftKeyboard
import com.google.android.material.snackbar.Snackbar
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@AndroidEntryPoint
@RuntimePermissions
class AddBoxFragment : Fragment(R.layout.add_box_fragment) {

    private val binding: AddBoxFragmentBinding by viewBinding(AddBoxFragmentBinding::bind)
    private val viewModel: AddBoxViewModel by viewModels()

    private val args: AddBoxFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textNoData.visibility = View.VISIBLE

        binding.buttonScan.setOnClickListener { scanQrCodeWithPermissionCheck() }

        binding.buttonAdd.setOnClickListener {
            viewModel.addBox(
                name = binding.editItemName.text?.toString()?.trim() ?: "",
                qrCodeText = args.qrCodeValue,
                region = args.region
            )
            closeSoftKeyboard(view)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.qrCode.collect { qrCode ->
                if (qrCode != null) {
                    binding.imageQrcode.setImageBitmap(qrCode)
                    binding.textNoData.visibility = View.INVISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.registrationResult.collect { result ->
                if (result != null) {
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(view.rootView, "Failed to add the box", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        args.qrCodeValue
            ?.also { qrCodeValue -> viewModel.genQrCode(requireContext(), qrCodeValue) }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun scanQrCode() {
        findNavController().navigate(
            AddBoxFragmentDirections.actionAddBoxFragmentToScanQrCodeFragment(args.region)
        )
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun requestPermissions() {
        Snackbar.make(
            requireView().rootView,
            "scanning needs a permission to use camera!",
            Snackbar.LENGTH_SHORT
        ).show()
    }

}