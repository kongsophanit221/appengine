package com.soteca.loyaltyuserengine.app

import android.content.Context
import android.util.Log
import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.helper.SharedPreferenceHelper
import soteca.com.genisysandroid.framwork.helper.crmFormatToDate
import soteca.com.genisysandroid.framwork.helper.crmFormatToString
import java.util.*

class AppToken() {

    companion object {

        fun removeFromStorage(context: Context) {
            SharedPreferenceHelper.getInstance(context).deleteAccessTokenDetail()
        }

        fun initFromSave(context: Context): AppToken? {

            val tokenFromStorage = SharedPreferenceHelper.getInstance(context).getAccessTokenDetail()

            if (tokenFromStorage == null) {
                return null
            }
            val expirationDate = tokenFromStorage["EXPIRATION_DATE"]!!.crmFormatToDate()

            return AppToken(tokenFromStorage["FIRST_TOKEN"]!!, tokenFromStorage["SECOND_TOKEN"]!!, tokenFromStorage["KEY_IDENTIFIER"]!!, expirationDate)
        }
    }

    private val TAG = "tAppToken"

    private var firstToken: String = ""
    private var secondToken: String = ""
    private var keyIdentifier: String = ""
    var expirationDate: Date? = null

    val content: Triple<String, String, String>?
        get() {
            if (expirationDate == null) {
                return null
            }

            return Triple(firstToken, secondToken, keyIdentifier)
        }

    constructor(firstToken: String, secondToken: String, keyIdentifier: String, expirationDate: Date) : this() {
        this.firstToken = firstToken
        this.secondToken = secondToken
        this.keyIdentifier = keyIdentifier
        this.expirationDate = expirationDate
    }

    constructor(str: String) : this() {
        try {
            val jsonObject = JSONObject(str)
            expirationDate = jsonObject.getSafeString("expired_at").crmFormatToDate()
            keyIdentifier = jsonObject.getSafeString("keyIdentifier")
            firstToken = jsonObject.getSafeString("securityToken0")
            secondToken = jsonObject.getSafeString("securityToken1")

        } catch (e: Exception) {
            Log.d("tAppToken", e.localizedMessage)
        }
    }

    fun saveToStorage(context: Context) {
        val accessTokenDetail: HashMap<String, String> = hashMapOf(
                "FIRST_TOKEN" to firstToken,
                "SECOND_TOKEN" to secondToken,
                "KEY_IDENTIFIER" to keyIdentifier,
                "EXPIRATION_DATE" to expirationDate!!.crmFormatToString())

        SharedPreferenceHelper.getInstance(context).setAccessTokenDetail(accessTokenDetail)
    }
}