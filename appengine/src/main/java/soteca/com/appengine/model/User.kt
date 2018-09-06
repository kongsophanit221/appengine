package com.soteca.loyaltyuserengine.model

import android.content.Context
import com.soteca.loyaltyuserengine.app.AppToken
import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.helper.SharedPreferenceHelper
import soteca.com.genisysandroid.framwork.helper.crmFormatToString
import soteca.com.genisysandroid.framwork.model.EntityReference
import java.util.HashMap

class User {

    companion object {

        private var user: User? = null

        @Throws(NullPointerException::class)
        fun current(): User {
            if (user == null) {
                throw NullPointerException("current use null")
            }
            return user!!
        }

        fun init(context: Context, json: String) {
            this.user = User(context, json)
        }

        fun updateAuth(context: Context, json: String) {
            this.user?.init(context, json)
        }
    }

    val contactId: String
        get() = this.contact.idcrm_contactid

    var cardId: String = ""
    var authToken: String = ""

    lateinit var card: Card
    lateinit var contact: Contact
    lateinit var appToken: AppToken
    private var context: Context

    val entityReference: EntityReference
        get() {
            return EntityReference(id = contactId, logicalName = "contact")
        }


    private constructor(context: Context, json: String) {
        this.context = context
        init(context, json)
    }

    private fun init(context: Context, json: String) {
        val appToken = AppToken(json)
        var authToken = ""

        try {
            authToken = JSONObject(json).getJSONObject("user").getSafeString("token")
            val cardsJSONArray = JSONObject(json).getJSONArray("cards")

            if (cardsJSONArray.length() > 0) {
                cardId = cardsJSONArray.getJSONObject(0).getSafeString("idcrm_cardid")
            }

        } catch (e: Exception) {

        }

        this.appToken = appToken
        this.authToken = authToken
        this.contact = Contact(json)

        appToken.saveToStorage(context)
    }

    fun updateCard(card: Card) {
        this.card = card
    }

    fun signOut() {
        user = null
    }
}