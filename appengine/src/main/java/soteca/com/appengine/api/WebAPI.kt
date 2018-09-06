package com.soteca.loyaltyuserengine.api

import android.content.Context
import com.soteca.loyaltyuserengine.app.AppRequestData
import com.soteca.loyaltyuserengine.app.AppRequestTask
import com.soteca.loyaltyuserengine.app.AppToken
import com.soteca.loyaltyuserengine.model.Contact
import com.soteca.loyaltyuserengine.model.User
import com.soteca.loyaltyuserengine.util.SharedPreferenceUtils
import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.networking.Request

class WebAPI {

    companion object {

        val CRM_URL = "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc"
        val USER_NAME = "hariservice.larotisserie@haricrm.com"
        val PASSWORD = "avm-!dT]PD?7{AZg"
        val LOGIN_URL = "https://larotisserie.haricrm.com/api/v1/contact/login"
        val APP_ID = "$2y$10\$jE6or0xcZczSj5EgDMA0ROcKcTzL9osP7xI8mW4jjZ7XxfDYH8f0u"

        private var _shared: WebAPI? = null

        fun shared(context: Context): WebAPI {
            if (_shared == null) {
                _shared = WebAPI(context)
            }
            return _shared!!
        }
    }

    private var context: Context
    private val WEB_URL = "https://larotisserie.haricrm.com/api/v1/contact"

    private val FORGET_PASSWORD_URL = WEB_URL + "/forgetpassword"
    private val UPDATE_PASSWORD_URL: String = WEB_URL + "/updatepassword"

    constructor(context: Context) {
        this.context = context
    }

    fun register(param: HashMap<String, String>?, handler: (AppRequestData, Boolean, String?) -> Unit) {
        val request = AppRequestData(WEB_URL, param)
        request.setHTTPMethod(Request.HTTPMethod.post)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(request, false, result.message)
                return@AppRequestTask
            }

            handler(request, true, null)
        }).execute()
    }

    fun login(context: Context, param: HashMap<String, String>?, done: (request: AppRequestData?, status: Boolean?, error: String?) -> Unit) {

        val request = AppRequestData(LOGIN_URL, param)

        val result = AppRequestTask(request).execute().get()

        if (result.isError()) {
            return done(request, false, result.message)
        }

        User.init(context, result.data)
        done(request, true, null)
    }

    fun deactive(contactId: String, handler: (AppRequestData, Boolean, String?) -> Unit) {

        val param = hashMapOf("guid" to contactId)
        val request = AppRequestData(WEB_URL, param)
        request.setHTTPMethod(Request.HTTPMethod.delete)
        request.addAuthutication(User.current().authToken)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(request, false, result.message)
                return@AppRequestTask
            }

            handler(request, true, null)
        }).execute()
    }

    fun contactInformation(contactId: String, handler: (AppRequestData, Contact?, String?) -> Unit) {

        val param = hashMapOf("guid" to contactId)
        val request = AppRequestData(WEB_URL, param)
        request.setHTTPMethod(Request.HTTPMethod.get)
        request.addAuthutication(User.current().authToken)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(request, null, result.message)
                return@AppRequestTask
            }

            handler(request, Contact(result.data), null)

        }).execute()
    }

    fun updateInformation(contactId: String, firstname: String, lastname: String, email: String, companycode: String, mobilephone: String, birthdate: String, handler: (AppRequestData, Boolean, String?) -> Unit) {
        var param = hashMapOf("guid" to contactId, "firstname" to firstname, "lastname" to lastname, "emailaddress1" to email,
                "idcrm_companycode" to companycode, "mobilephone" to mobilephone, "birthdate" to birthdate, "card_guid" to User.current().cardId)

        val request = AppRequestData(WEB_URL, param)
        request.setHTTPMethod(Request.HTTPMethod.put)
        request.addAuthutication(User.current().authToken)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(request, false, result.message)
                return@AppRequestTask
            }

            handler(request, true, null)
        }).execute()
    }

    fun changePassword(contactId: String, oldPassword: String, newPassword: String, handler: (AppRequestData, Boolean, String?) -> Unit) {
        val param = hashMapOf("guid" to contactId, "password" to newPassword, "password_old" to oldPassword)
        val request = AppRequestData(UPDATE_PASSWORD_URL, param)
        request.setHTTPMethod(Request.HTTPMethod.post)
        request.addAuthutication(User.current().authToken)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(request, false, result.message)
                return@AppRequestTask
            }

            handler(request, true, null)
        }).execute()
    }

    fun forgetPassword(email: String, handler: (AppRequestData, Boolean, String?) -> Unit) {
        val param = hashMapOf("emailaddress1" to email)
        val request = AppRequestData(FORGET_PASSWORD_URL, param)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(request, false, result.message)
                return@AppRequestTask
            }

            handler(request, true, null)

        }).execute()
    }


}