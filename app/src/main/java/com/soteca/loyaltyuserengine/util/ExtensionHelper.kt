package com.soteca.loyaltyuserengine.util

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.soteca.loyaltyuserengine.model.Datasource

class ExtensionHelper {

}

enum class ImageScaleType {
    SMALL, MEDIUM;

    fun subjectName(): String {

        val density = Resources.getSystem().displayMetrics.density

        return when (this) {
            ImageScaleType.SMALL -> return "small_${density.toInt()}"
            ImageScaleType.MEDIUM -> return "medium_${density.toInt()}"
        }
    }
}

/*
fun Resources.screenScale(): Scale {

    val density = this.displayMetrics.density

    when (density) {
        in 1.5..2.0 -> return Scale.SMALL
        in 2.0..3.0 -> return Scale.MEDIUM
        in 3.0..4.0 -> return Scale.BIG
    }

    return Scale.SMALL
}*/
