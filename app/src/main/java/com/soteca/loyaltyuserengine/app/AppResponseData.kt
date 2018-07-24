package com.soteca.loyaltyuserengine.app

import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject

class AppResponseData() {

    private var code: String = ""
    var message: String = ""
    var data: String = ""

    constructor(data: String?) : this() {

        if (data != null) {
            val jsonObject = JSONObject(data)
            this.code = jsonObject.getSafeString("code")
            this.message = jsonObject.getSafeString("message")

            if (!jsonObject.isNull("data")) {
                this.data = jsonObject.getJSONObject("data").getSafeString("session")
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