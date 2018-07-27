package com.soteca.loyaltyuserengine.util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class SharedPreferenceHelper(ctx: Context) {

    private val ORDER_ID = "ORDER_ID"

    companion object {
        private var prefs: SharedPreferences? = null
        private var prefsHelper: SharedPreferenceHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): SharedPreferenceHelper {
            if (prefsHelper == null) {
                prefsHelper = SharedPreferenceHelper(ctx)
                prefs = ctx.getSharedPreferences("loyaltyuserengine", 0)
            }
            return prefsHelper!!
        }
    }

    /*
    * ORDER_ID
    *
    * */
    fun setOrderId(orderId: String) {
        val editor = prefs!!.edit()
        editor.putString(ORDER_ID, orderId)
        editor.apply()
    }

    fun getOrderId(): String? {
        val orderId = prefs!!.getString(ORDER_ID, "")
        if (orderId == "") {
            return null
        }
        return orderId
    }

    fun deleteOderId() {
        prefs!!.edit().remove(ORDER_ID).apply()
    }
}