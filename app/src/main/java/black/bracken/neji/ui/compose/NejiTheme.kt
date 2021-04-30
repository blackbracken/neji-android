package black.bracken.neji.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun NejiTheme(content: @Composable () -> Unit) {
    val colors = if (!isSystemInDarkTheme()) lightColors() else darkColors()

    MaterialTheme(
        colors = colors,
    ) {
        content()
    }
}