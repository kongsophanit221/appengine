package com.soteca.loyaltyuserengine.networking

import android.net.Uri
import soteca.com.genisysandroid.framwork.model.encoder.Encoder
import soteca.com.genisysandroid.framwork.networking.Request
import java.net.URL
import java.nio.charset.Charset

class AppRequestData(private val urlString: String, private val param: HashMap<String, String>? = null) : Request {

    override val requestUrl: URL
        get() = URL(urlString)

    override val path: String
        get() = ""

    override val httpMethod: Request.HTTPMethod
        get() = Request.HTTPMethod.post

    override val header: HashMap<String, String>
        get() = hashMapOf()
    //hashMapOf("Content-type" to "application/soap+xml; charset=UTF-8",
//                "Host" to requestUrl.host,
//                "Content-Length" to contentLenght.toString(),
//                "Connection" to "Keep-Alive")

    override val parameter: ByteArray
        get() {
            val builder = Uri.Builder()
            param?.forEach {
                builder.appendQueryParameter(it.key, it.value)
            }

            return builder.build().getEncodedQuery()?.let { it.toByteArray(Charsets.UTF_8) }
                    ?: "".toByteArray(Charsets.UTF_8)
        }

    override val timeout: Int
        get() = 3000

}