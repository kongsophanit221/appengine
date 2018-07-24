package com.soteca.loyaltyuserengine.app

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebConfig
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import soteca.com.genisysandroid.framwork.authenticator.Authenticator
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.helper.SharedPreferenceHelper
import java.util.*

class AppAuthenticator : Authenticator {

    private val KEY = "APP_AUTH"

    private lateinit var configuration: DynamicsConfiguration
    lateinit var context: Context
    private var email: String = ""
    private var password: String = ""
    private var token: AppToken? = null

    override val crmUrl: String
        get() = WebConfig.shared().CRM_URL

    companion object {

        fun initFromStorage(context: Context): AppAuthenticator? {
            val auth = AppAuthenticator(context)
            auth.getInfoFromStorage()

            if (auth.token == null && auth.email == "" && auth.password == "") {
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

    }

    override fun clearSecurityToken() {
        token = null
        AppToken.removeFromStorage(context)
    }


    private fun retrievedToken(): AppToken? = runBlocking {

        async {
            val param = hashMapOf("emailaddress1" to email, "idcrm_password" to password)

            val result = Datasource.shared(this@AppAuthenticator.context).login(param)

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
}