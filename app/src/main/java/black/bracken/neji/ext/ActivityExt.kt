package black.bracken.neji.ext

import android.app.Activity
import android.graphics.Point

private val Activity.screenPoint: Point
    get() {
        val display = windowManager.defaultDisplay
        val point = Point(0, 0)
        display.getRealSize(point)

        return point
    }

val Activity.screenWidth: Int
    get() = screenPoint.x

val Activity.screenHeight: Int
    get() = screenPoint.y