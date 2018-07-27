package com.soteca.loyaltyuserengine.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by SovannMeasna on 5/4/18.
 */
class SharedPreferenceUtils(context: Context) {

    private val CONTACT_ID = "contact_id"

    companion object {

        private var prefs: SharedPreferences? = null
        private var prefsHelper: SharedPreferenceUtils? = null

        @Synchronized
        fun shared(ctx: Context): SharedPreferenceUtils {
            if (prefsHelper == null) {
                prefsHelper = SharedPreferenceUtils(ctx)
                prefs = ctx.getSharedPreferences("app_engine", 0)
            }
            return prefsHelper!!
        }
    }

    fun setUserId(userId: String) {
        val editor = prefs!!.edit()
        editor.putString(CONTACT_ID, userId)
        editor.apply()
    }

    fun getUserId(): String {
        return prefs!!.getString(CONTACT_ID, "")
    }
}