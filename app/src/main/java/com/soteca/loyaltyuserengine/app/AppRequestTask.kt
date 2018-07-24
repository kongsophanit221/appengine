package com.soteca.loyaltyuserengine.app

import android.os.AsyncTask
import android.util.Log
import soteca.com.genisysandroid.framwork.networking.Request
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection

/**
 * Created by SovannMeasna on 4/6/18.
 */
class AppRequestTask(val request: Request, val done: ((result: AppResponseData) -> Unit)? = null) : AsyncTask<String, String, AppResponseData>() {

    private val TAG = "tRequest"
    private var connection: HttpURLConnection? = null

    override fun doInBackground(vararg params: String?): AppResponseData {
        var inputStream: InputStream? = null

        connection = request!!.requestUrl.openConnection() as HttpURLConnection
        connection!!.requestMethod = request!!.httpMethod.value
        connection!!.connectTimeout = request!!.timeout
        connection!!.doOutput = true
        connection!!.useCaches = false

        val postData: ByteArray = request!!.parameter!!
        val content = postData.toString(Charsets.UTF_8)
        for ((key, value) in request!!.header) {
            connection!!.setRequestProperty(key, value)
        }
        try {
            val outputStream = connection!!.outputStream
            outputStream.write(postData)
            outputStream.flush()

            if (connection!!.responseCode != HttpURLConnection.HTTP_OK && connection!!.responseCode != HttpURLConnection.HTTP_CREATED) {
                inputStream = connection!!.errorStream
//                val errorText = streamToString(inputStream)!!
//                Log.d(TAG, errorText)
            } else {
                inputStream = connection!!.inputStream
            }

        } catch (exception: Exception) {
            Log.d(TAG, "request: " + request!!.requestUrl)
            Log.e(TAG, "connection exception localizedMessage: " + exception.localizedMessage)
            Log.e(TAG, "connection exception message: " + exception.message)
        } finally {

            if (inputStream == null) {
                return AppResponseData().initNilError()
            }

            return AppResponseData(streamToString(inputStream))
        }
    }

    override fun onPostExecute(result: AppResponseData) {
        super.onPostExecute(result)
        this.done?.let {
            it(result)
        }
    }

    private fun closeConnection() {
        connection?.disconnect()
    }

    private fun streamToString(inputStream: InputStream): String? {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {
//            Log.e(TAG, "streamToString: " + ex.localizedMessage)
        }
        return result
    }
}