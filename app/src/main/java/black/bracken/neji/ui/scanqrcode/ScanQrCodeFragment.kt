package black.bracken.neji.ui.scanqrcode

import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.ScanQrCodeFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanQrCodeFragment : Fragment(R.layout.scan_qr_code_fragment) {

    private val binding by viewBinding(ScanQrCodeFragmentBinding::bind)
    private val args: ScanQrCodeFragmentArgs by navArgs()

    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { preview ->
                    preview.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor, QrCodeAnalyzer { text ->
                        println("QRCode scanned: $text")

                        val action = ScanQrCodeFragmentDirections
                            .actionScanQrCodeFragmentToAddBoxFragment(args.region, text)
                        requireView().post {
                            findNavController().navigate(action)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (ex: Exception) {
                // TODO: handle errors
                ex.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

}