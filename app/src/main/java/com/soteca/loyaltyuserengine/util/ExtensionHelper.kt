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
    return when (this) {
        in 0.0..1.5 -> return 1
        in 1.5..2.5 -> return 2
        else -> 3
    }
}
