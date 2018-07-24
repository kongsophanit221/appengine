package com.soteca.loyaltyuserengine.util

import android.content.res.Resources
import android.media.Image
import android.util.Log
import android.widget.ImageView
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.authenticator.DynamicAuthenticator
import soteca.com.genisysandroid.framwork.helper.crmFormatToDate

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

fun DynamicAuthenticator.Token.initWithJson(str: String): DynamicAuthenticator.Token {
    
    try {
        val jsonObject = JSONObject(str)
        val expiredDate = jsonObject.getSafeString("expired_at").crmFormatToDate()
        val keyIdentifier = jsonObject.getSafeString("keyIdentifier")
        val securityToken0 = jsonObject.getSafeString("securityToken0")
        val securityToken1 = jsonObject.getSafeString("securityToken1")

        return DynamicAuthenticator.Token(securityToken0, securityToken1, keyIdentifier, expiredDate)

    } catch (e: Exception) {
        val ee = e
        return DynamicAuthenticator.Token()
    }
}
