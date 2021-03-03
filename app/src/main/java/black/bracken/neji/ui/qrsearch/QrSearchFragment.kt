package black.bracken.neji.ui.qrsearch

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import black.bracken.neji.R
import black.bracken.neji.databinding.QrSearchFragmentBinding
import com.wada811.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class QrSearchFragment : Fragment(R.layout.qr_search_fragment) {

    private val binding by viewBinding(QrSearchFragmentBinding::bind)
    private val viewModel: QrSearchViewModel by viewModels()
    private val args: QrSearchFragmentArgs by navArgs()

    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { preview ->
                    preview.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
                }

            val barcodeEmphasize = ImageAnalysis.Builder()
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(
                        cameraExecutor,
                        MultiQrCodeAnalyzer { imageWidth, imageHeight, barcodes ->
                            val barcode = barcodes.find { it.rawValue == args.targetBox.qrCodeText }
                                ?: return@MultiQrCodeAnalyzer

                            val bitmap = binding.viewFinder.bitmap ?: return@MultiQrCodeAnalyzer
                            val overlay = Bitmap.createBitmap(
                                bitmap.width,
                                bitmap.height,
                                Bitmap.Config.ARGB_8888
                            )
                            val paint = Paint().apply {
                                isAntiAlias = true
                                style = Paint.Style.STROKE
                                color = Color.RED
                                strokeWidth = 10f
                            }
                            val resolution = preview.attachedSurfaceResolution
                                ?: return@MultiQrCodeAnalyzer

                            val fixX = resolution.width / imageWidth.toFloat()
                            val fixY = resolution.height / imageHeight.toFloat()

                            Canvas(overlay).apply {
                                val corners = barcode.cornerPoints
                                    ?: return@MultiQrCodeAnalyzer

                                // TODO: fit position
                                corners.forEach {
                                    drawPoint(it.x * fixX, it.y * fixY, paint)
                                }
                            }

                            activity?.runOnUiThread {
                                binding.overlay.setImageBitmap(overlay)
                            }
                        })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, barcodeEmphasize)
            } catch (ex: Exception) {
                // TODO: handle errors
                ex.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

}