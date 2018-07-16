package com.soteca.loyaltyuserengine.util

import android.content.res.Resources
import android.media.Image
import android.widget.ImageView

class ExtensionHelper {

}

enum class ImageScaleType {
    SMALL, MEDIUM;

    fun subjectName(): String {

        val density = Resources.getSystem().displayMetrics.density

        return when (this) {
            ImageScaleType.SMALL -> return "small_${density.screenScale()}"
            ImageScaleType.MEDIUM -> return "medium_${density.screenScale()}"
        }
    }
}

fun Float.screenScale(): Int {

    val density = Resources.getSystem().displayMetrics.density

    return when (density) {
        in 1.5..2.0 -> return 1
        in 2.0..3.0 -> return 2
        in 3.0..4.0 -> return 3
        else -> 0
    }
}
