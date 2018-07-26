package com.soteca.loyaltyuserengine.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(ctx: Context) {

    private val ORDER_ID = "CART_ID"

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
    @SuppressLint("CommitPrefEdits")
    fun setOrderId(cartId: String) {
        val editor = prefs!!.edit()
        editor.putString(ORDER_ID, cartId)
        editor.apply()
    }

    fun getOrderId(): String? {
        val cartId = prefs!!.getString(ORDER_ID, "")
        if (cartId == "") {
            return null
        }
        return cartId
    }

    fun deleteOderId() {
        prefs!!.edit().remove(ORDER_ID).apply()
    }
}