package com.soteca.loyaltyuserengine.app

import android.net.Uri
import com.soteca.loyaltyuserengine.api.WebAPI
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.networking.Request
import java.lang.reflect.Method
import java.net.URL

class AppRequestData(private val url: String, private val param: HashMap<String, String>? = null) : Request {

    private var method = Request.HTTPMethod.post
    private var headerValue = hashMapOf("X-APP-ID" to WebAPI.APP_ID, "Content-Type" to "application/json")
    private val urlString: String
        get() {
            if (method == Request.HTTPMethod.get) {

                var builtUri = Uri.parse(url)

                param?.forEach {
                    builtUri = builtUri.buildUpon().appendQueryParameter(it.key, it.value).build()
                }

                return builtUri.toString()
            }
            return url
        }

    override val requestUrl: URL
        get() = URL(urlString)

    override val path: String
        get() = ""

    override val httpMethod: Request.HTTPMethod
        get() = method

    override val header: HashMap<String, String>
        get() = headerValue

    override val parameter: ByteArray
        get() {
            val jsonObject = JSONObject(param)
            return jsonObject.toString().toByteArray(Charsets.UTF_8)

        }

    override val timeout: Int
        get() = 3000

    fun setHTTPMethod(method: Request.HTTPMethod) {
        this.method = method
    }

    fun addAuthutication(value: String) {
        this.headerValue["X-Authorization"] = value
    }

}