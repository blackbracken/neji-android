package black.bracken.neji.ext.viewcomponent

import android.view.View
import com.google.android.material.progressindicator.ProgressIndicator

fun ProgressIndicator.disableAndHide() {
    isIndeterminate = false
    visibility = View.GONE
}
