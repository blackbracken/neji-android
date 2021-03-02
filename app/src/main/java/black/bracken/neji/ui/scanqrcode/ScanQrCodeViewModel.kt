package black.bracken.neji.ui.scanqrcode

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class ScanQrCodeViewModel @ViewModelInject constructor() : ViewModel()

class QrCodeAnalyzer(private val onScan: (String) -> Unit) : ImageAnalysis.Analyzer {

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
            .addOnCompleteListener { proxy.close() }
    }

}