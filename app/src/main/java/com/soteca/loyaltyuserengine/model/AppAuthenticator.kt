package com.soteca.loyaltyuserengine.model

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebConfig
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import soteca.com.genisysandroid.framwork.authenticator.Authenticator
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.networking.AuthenticationError

class AppAuthenticator : Authenticator {

    private lateinit var configuration: DynamicsConfiguration
    lateinit var context: Context
    private var email: String = ""
    private var password: String = ""

    override val crmUrl: String
        get() = WebConfig.shared().CRM_URL

    constructor() : super()

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, email: String, password: String) : this(context) {
        this.email = email
        this.password = password
    }

    override fun setConfiguration(configuration: DynamicsConfiguration) {
        this.configuration = configuration
    }

    override fun authenticate(): Triple<String, String, String> {
        return preExecutionCheck()!!
    }

    override fun clearConfiguration() {
        super.clearConfiguration()
    }

    override fun clearSecurityToken() {
        super.clearSecurityToken()
    }


    private fun preExecutionCheck(): Triple<String, String, String>? = runBlocking {

        //        if (authenticator == null) {
//            throw AuthenticationError.invalidSecurityToken.error
//        }
        async {
            val param = hashMapOf("emailaddress1" to email, "idcrm_password" to password)

            val result = Datasource.shared(this@AppAuthenticator.context).login(param)
            if (result.second != null) {
                null
            }
            result.first?.content!!


        }.await() //securityContent
    }

}