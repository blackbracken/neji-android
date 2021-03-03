package black.bracken.neji.util

import android.content.Context
import android.graphics.Bitmap
import black.bracken.neji.R
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

fun createQrCode(context: Context, text: String): Bitmap {
    val size = context.resources.getDimension(R.dimen.qrcode_size).toInt()

    return BarcodeEncoder().encodeBitmap(text, BarcodeFormat.QR_CODE, size, size)
}