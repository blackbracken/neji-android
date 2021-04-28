package black.bracken.neji.ext.viewcomponent

import android.view.View
import com.google.android.material.progressindicator.LinearProgressIndicator

fun LinearProgressIndicator.disableAndHide() {
    isIndeterminate = false
    visibility = View.GONE
}