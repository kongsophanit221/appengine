package soteca.com.genisysandroid.framwork.authenticator

import android.content.Context
import android.util.Log
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.helper.SharedPreferenceHelper
import soteca.com.genisysandroid.framwork.helper.crmFormatToDate
import soteca.com.genisysandroid.framwork.helper.crmFormatToString
import soteca.com.genisysandroid.framwork.model.decoder.DeviceTokenDecoder
import soteca.com.genisysandroid.framwork.model.decoder.StringDecoder
import soteca.com.genisysandroid.framwork.model.decoder.TokenDecoder
import soteca.com.genisysandroid.framwork.model.encoder.DeviceAddRequestEncoder
import soteca.com.genisysandroid.framwork.model.encoder.EnvelopeEncoder
import soteca.com.genisysandroid.framwork.model.encoder.body.BodyEncoder
import soteca.com.genisysandroid.framwork.model.encoder.body.RequestSecurityTokenEncoder
import soteca.com.genisysandroid.framwork.model.encoder.header.HeaderEncoder
import soteca.com.genisysandroid.framwork.networking.AuthenticationError
import soteca.com.genisysandroid.framwork.networking.Errors
import soteca.com.genisysandroid.framwork.networking.RequestTask
import java.util.*


/**
 * Created by SovannMeasna on 3/27/18.
 */
class Authenticator() {

    private val TAG = "tAuth"

    companion object {
        var context: Context? = null
        val sharedPreference by lazy {
            SharedPreferenceHelper.getInstance(context!!)
        }
    }

    private var configuration: DynamicsConfiguration? = null //DynamicsConfiguration = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365)
    private var token: Token? = null

    val crmUrl: String
        get() {
            return this.configuration!!.crmUrl
        }

    constructor(context: Context) : this() {
        Authenticator.context = context
        this.configuration = DynamicsConfiguration()
        this.token = Token(context)
    }

    constructor(context: Context, configuration: DynamicsConfiguration) : this(context) {

        Token().removeFromStorage(context)
        DynamicsConfiguration.removeFromStorage(context)

        Authenticator.context = context
        this.configuration = configuration
    }

    fun clearSecurityToken() {
        token = null
        Token().removeFromStorage(context!!)
    }

    fun authenticate(): Triple<String, String, String> {

        val content = token!!.content

        if (content != null) {
            return content
        }

        val deviceToken = getDeviceToken() ?: throw AuthenticationError.failedDeviceTokenAcquisition.error

        try {
            token = getSecurityDetail(deviceToken)
        } catch (e: Exception) {
            Log.d(TAG, "authenticate: " + e.localizedMessage)
            throw e
        }

        token!!.saveToStorage(context!!)
        configuration!!.save(context!!)

        return token!!.content!!
    }

    private fun getSecurityDetail(deviceToken: String): Token? {

        val accessTokenDetail: Token?
        val envelop = EnvelopeEncoder(HeaderEncoder.getSecurityToken("https://login.microsoftonline.com/liveidSTS.srf", configuration!!, deviceToken), BodyEncoder(RequestSecurityTokenEncoder.securityToken(configuration!!.urnAddress)))//crmemea
        val request = AuthenticatorRequest(configuration!!.loginHost, envelop, AuthenticatorRequest.Type.securityTokenAcquisition)
        val tokenDecoder = TokenDecoder(request)

        val result = RequestTask<TokenDecoder>(tokenDecoder).execute().get()

//        try {

            if (result.second != null) {
                throw result.second!!.error!!.error
            }

            accessTokenDetail = result.first!!.token
            Log.d(TAG, "accessTokenDetail: " + accessTokenDetail)

//        } catch (e: Exception) {
//            Log.e(TAG, "getSecurityDetail: " + e.localizedMessage)
//        }

//        val soapEnvelope = SoapEnvelope(deviceToken, configuration!!, SoapEnvelope.SoapEnvelopeType.securityToken)
//        val request = AuthenticatorRequest(configuration!!.loginHost, soapEnvelope, AuthenticatorRequest.SoapAuthenticatorRequestType.deviceTokenAcquisition)
//        val result = RequestTask<TokenDecoder>(request, TokenDecoder()).execute().get()
//
//        try {
//
//            if (result.second != null) {
//                throw result.second!!.error!!.error
//            }
//
//            accessTokenDetail = result.first!!.token
//            Log.d(TAG, "accessTokenDetail: " + accessTokenDetail)
//
//        } catch (e: Exception) {
//            Log.e(TAG, "json: " + e.localizedMessage)
//        }

        return accessTokenDetail
    }

    private fun getDeviceToken(): String? {

        fun getDeviceToken(credential: HashMap<String, String>): String? {

            var token = ""
            val envelop = EnvelopeEncoder(HeaderEncoder.getDeviceToken("https://login.microsoftonline.com/liveidSTS.srf", credential), BodyEncoder(RequestSecurityTokenEncoder.deviceToken("http://passport.net/tb")))
            val request = AuthenticatorRequest(configuration!!.loginHost, envelop, AuthenticatorRequest.Type.deviceTokenAcquisition)
            val deviceTokenDecoder = DeviceTokenDecoder(request)

            val result = RequestTask<DeviceTokenDecoder>(deviceTokenDecoder).execute().get()

            try {
                if (result.second != null) {
                    Log.e(TAG, "response: " + result.second!!.error!!.error.localizedMessage)
                    return ""
                }

                token = result.first!!.deviceToken!!

            } catch (e: Exception) {
                Log.e(TAG, "json: " + e.localizedMessage)
            }

            return token
        }

        fun registerDevice(): HashMap<String, String>? {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            val timeInterval = calendar.timeInMillis / 1000L

            var deviceCredential: HashMap<String, String>? = hashMapOf(
                    "deviceName" to "${timeInterval}2u91rlobl7lvkihb",
                    "devicePassword" to "GRB+WGz`eCkr-k${timeInterval}")

            val deviceAddRequestEncoder = DeviceAddRequestEncoder(deviceCredential)
            val request = AuthenticatorRequest(configuration!!.loginHost, deviceAddRequestEncoder, AuthenticatorRequest.Type.deviceRegistration)
            val registerDeviceDecoder = StringDecoder(request)

            val result = RequestTask<StringDecoder>(registerDeviceDecoder).execute().get()

            if (result.second != null) {
                deviceCredential = null
            }

            return deviceCredential!!
        }

        val deviceCredential = registerDevice()

        if (deviceCredential != null) {
            return getDeviceToken(deviceCredential)
        }
        return null
    }

    class Token() {

        private val TAG = "tAccessTokenDetail"

        private var firstToken: String = ""
        private var secondToken: String = ""
        private var keyIdentifier: String = ""
        private var expirationDate: Date? = null

        val content: Triple<String, String, String>?
            get() {

                if (expirationDate == null) {
                    return null
                }
                Log.d("tExpire", "expirationDate: ${expirationDate!!}")
                Log.d("tExpire", "NOW: ${Date()}")

                if (expirationDate!!.time > Date().time) {
                    return Triple(firstToken, secondToken, keyIdentifier)
                }
                return null
            }

        constructor(context: Context) : this() {

            val tokenFromStorage = sharedPreference.getAccessTokenDetail()

            if (tokenFromStorage == null) {
                return
            }
            val expirationDate = tokenFromStorage["EXPIRATION_DATE"]!!.crmFormatToDate()

            if (expirationDate.time < Date().time) {
                removeFromStorage(context)
                return
            }

            this.expirationDate = expirationDate
            this.firstToken = tokenFromStorage["FIRST_TOKEN"]!!
            this.secondToken = tokenFromStorage["SECOND_TOKEN"]!!
            this.keyIdentifier = tokenFromStorage["KEY_IDENTIFIER"]!!
        }

        constructor(firstToken: String, secondToken: String, keyIdentifier: String, expirationDate: Date) : this() {
            this.firstToken = firstToken
            this.secondToken = secondToken
            this.keyIdentifier = keyIdentifier
            this.expirationDate = expirationDate
        }

        override fun toString(): String {
            return "firstToken: $firstToken, secondToken: $secondToken, keyIdentifier: $keyIdentifier, expirationDate: $expirationDate"
        }

        fun removeFromStorage(context: Context) {
            SharedPreferenceHelper.getInstance(context).deleteAccessTokenDetail()
        }

        fun saveToStorage(context: Context) {
            val accessTokenDetail: HashMap<String, String> = hashMapOf("FIRST_TOKEN" to firstToken,
                    "SECOND_TOKEN" to secondToken,
                    "KEY_IDENTIFIER" to keyIdentifier,
                    "EXPIRATION_DATE" to expirationDate!!.crmFormatToString())

            SharedPreferenceHelper.getInstance(context).setAccessTokenDetail(accessTokenDetail)
        }
    }
}
