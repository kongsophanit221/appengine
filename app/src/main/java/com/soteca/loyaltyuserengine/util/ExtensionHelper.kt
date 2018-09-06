package com.soteca.loyaltyuserengine.util

import android.content.res.Resources
import org.json.JSONObject

class ExtensionHelper {

}

enum class ImageScaleType {
    SMALL, MEDIUM;

    fun subjectName(): String {

        val density = Resources.getSystem().displayMetrics.density

        when (this) {
            ImageScaleType.SMALL -> return "small_${density.screenScale()}"
            ImageScaleType.MEDIUM -> return "medium_${density.screenScale()}"
        }
    }
}

fun Float.screenScale(): Int {
    return when (this) {
        in 0.0..1.0 -> return 1
        in 1.1..2.5 -> return 2
        else -> 3
    }
}

fun JSONObject.getSafeString(key: String): String {
    try {
        if (this.has(key)) {
            val value = this.get(key).toString()
            if (value == "null") {
                return ""
            }
            return value
        } else {
            return ""
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

/**
 * Double
 */

fun Double.toStringDisplay(): String {
    if ((this == Math.floor(this)) && !this.isInfinite()) {
        return String.format("%.0f", this)
    } else {
        return String.format("%.2f", this)
    }
}
