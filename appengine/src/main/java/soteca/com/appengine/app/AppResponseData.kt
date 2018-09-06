package com.soteca.loyaltyuserengine.app

import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject

class AppResponseData() {

    private var code: String = ""
    var message: String = ""
    var timestamp: String = ""
    var data: String = ""

    constructor(data: String?) : this() {

        if (data != null) {

            try {
                val jsonObject = JSONObject(data).getJSONObject("message")
                this.code = jsonObject.getSafeString("ErrorCode")
                this.message = jsonObject.getSafeString("Message")
                this.timestamp = jsonObject.getSafeString("Timestamp")
                this.data = data

            } catch (e: Exception) {
                this.code = "1"
                this.message = data
            }
        }
    }

    private constructor(code: String, message: String) : this() {
        this.code = code
        this.message = message
    }

    fun isError(): Boolean {
        return this.code != "0"
    }

    fun initNilError(): AppResponseData {
        return AppResponseData("1", "Nil Response")
    }

    override fun toString(): String {
        return "code: $code, message: $message, data: $data"
    }
}