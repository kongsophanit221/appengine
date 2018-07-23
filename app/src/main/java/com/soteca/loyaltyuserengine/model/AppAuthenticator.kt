package com.soteca.loyaltyuserengine.model

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebConfig
import soteca.com.genisysandroid.framwork.authenticator.Authenticator
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration

class AppAuthenticator : Authenticator {

    private lateinit var configuration: DynamicsConfiguration

    override val crmUrl: String
        get() = WebConfig.shared().CRM_URL

    constructor() : super()

    constructor(context: Context) : super(context)

    override fun setConfiguration(configuration: DynamicsConfiguration) {
        this.configuration = configuration
    }

    override fun authenticate(): Triple<String, String, String> {
        //save info
        return super.authenticate()
    }

    override fun clearConfiguration() {
        super.clearConfiguration()
    }

    override fun clearSecurityToken() {
        super.clearSecurityToken()
    }

}