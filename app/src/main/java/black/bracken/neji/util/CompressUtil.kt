package black.bracken.neji.util

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import java.io.File

suspend fun ViewModel.compressImage(context: Context, uri: Uri): File {
    return Compressor.compress(context, uri.toFile(), viewModelScope.coroutineContext) {
        default(
            width = 240,
            height = 240
        )
    }
}