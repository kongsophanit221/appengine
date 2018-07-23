package com.soteca.loyaltyuserengine.api

class WebConfig {

    companion object {
        private var _shared: WebConfig? = null

        fun shared(): WebConfig {
            if (_shared == null) {
                _shared = WebConfig()
            }
            return _shared!!
        }
    }
    private val WEB_URL = "https://larotisserie.haricrm.com/api/v1/contact/"
    val REGISTER_URL = WEB_URL + "create"
    val LOGIN_URL = WEB_URL + "login"

    val CRM_URL = "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc"
    val USER_NAME = "hariservice.larotisserie@haricrm.com"
    val PASSWORD = "avm-!dT]PD?7{AZg"
}