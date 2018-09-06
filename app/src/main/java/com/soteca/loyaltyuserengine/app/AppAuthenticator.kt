package com.soteca.loyaltyuserengine.app

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebAPI
import com.soteca.loyaltyuserengine.model.Contact
import com.soteca.loyaltyuserengine.model.User
import com.soteca.loyaltyuserengine.util.SharedPreferenceUtils
import com.soteca.loyaltyuserengine.util.getSafeString
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.authenticator.Authenticator
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.helper.SharedPreferenceHelper
import java.util.*

class AppAuthenticator : Authenticator {

    private val KEY = "APP_AUTH"

    private var configuration: DynamicsConfiguration? = null
    lateinit var context: Context
    var email: String = ""
    var password: String = ""
    private var token: AppToken? = null

    override val crmUrl: String
        get() = WebAPI.CRM_URL

    companion object {

        fun initFromStorage(context: Context): AppAuthenticator? {
            val auth = AppAuthenticator(context)
            auth.getInfoFromStorage()

            if (auth.email == "" && auth.password == "") {
                return null
            }
            return auth
        }
    }

    constructor() : super()

    constructor(context: Context) : super(context) {
        this.context = context
        token = AppToken.initFromSave(context)
    }

    constructor(context: Context, email: String, password: String) : this(context) {
        this.email = email
        this.password = password
    }

    override fun setConfiguration(configuration: DynamicsConfiguration) {
        this.configuration = configuration
    }

    override fun authenticate(): Triple<String, String, String> {

        if (token == null) {
            token = retrievedToken()
            token?.saveToStorage(context)
        }

        if (token!!.expirationDate!!.time < Date().time) {
            AppToken.removeFromStorage(context)
            token = null
            token = retrievedToken()
            token?.saveToStorage(context)
        }

        return token?.content!!
    }

    override fun clearConfiguration() {
        configuration = null
        DynamicsConfiguration.removeFromStorage(context)
    }

    override fun clearSecurityToken() {
        token = null
        delete()
    }


    private fun retrievedToken(): AppToken? = runBlocking {

        async {
            val param = hashMapOf("emailaddress1" to email, "password" to password)

            val result = login(this@AppAuthenticator.context, param)

            if (result.second != null) {
                null
            }
            result.first
        }.await()
    }

    fun saveToStorage() {
        val hashMap = hashMapOf("email" to email, "password" to password)
        SharedPreferenceHelper.getInstance(context).setValues(KEY, hashMap)
    }

    private fun getInfoFromStorage() {
        val hashMap = SharedPreferenceHelper.getInstance(context).getValues(KEY)
        hashMap?.let {
            this.email = it["email"] ?: ""
            this.password = it["password"] ?: ""
        }
    }

    private fun delete() {
        AppToken.removeFromStorage(context)
        SharedPreferenceHelper.getInstance(context).deleteValue(KEY)

        token = null
        email = ""
        password = ""
    }

    fun isValideToken(): Boolean {

        if (token == null) {
            return false
        }

        if (token!!.expirationDate!!.time < Date().time) {
            return false
        }

        return true
    }

    private fun login(context: Context, param: HashMap<String, String>?): Pair<AppToken?, String?> {

        val request = AppRequestData(WebAPI.LOGIN_URL, param)

        val result = AppRequestTask(request).execute().get()

        if (result.isError()) {
            return null to result.message
        }

        User.updateAuth(context, result.data)

        return AppToken(result.data) to null
    }
}