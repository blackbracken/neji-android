package black.bracken.neji.ui.scanqrcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import black.bracken.neji.R
import black.bracken.neji.databinding.ScanQrCodeFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.scan_qr_code_fragment.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanQrCodeFragment : Fragment(R.layout.scan_qr_code_fragment) {

    private val viewModel: ScanQrCodeViewModel by viewModels()
    private val binding by viewBinding(ScanQrCodeFragmentBinding::bind)

    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        }

        cameraExecutor // initialize
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { preview ->
                    preview.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor, QrCodeAnalyzer { text ->
                        // TODO: implement on scanning
                        Snackbar.make(
                            requireView().rootView,
                            "scanned: $text",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                // TODO: handle errors
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { permission ->
        requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 195
        private val REQUIRED_PERMISSIONS = listOf(Manifest.permission.CAMERA)
    }

    private class QrCodeAnalyzer(private val onScan: (String) -> Unit) : ImageAnalysis.Analyzer {

        private val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        @SuppressLint("UnsafeExperimentalUsageError")
        override fun analyze(proxy: ImageProxy) {
            val media = proxy.image ?: return
            val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { qrCodes ->
                    qrCodes
                        .firstOrNull()
                        ?.rawValue
                        ?.also { text -> onScan(text) }
                }
                .addOnCompleteListener {
                    proxy.close()
                }
        }

    }

}