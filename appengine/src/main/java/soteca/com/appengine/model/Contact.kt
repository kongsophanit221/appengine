package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.helper.crmFormatToDate
import java.text.SimpleDateFormat
import java.util.*

class Contact() {

//    private val KEY = "Contact"

    var firstname: String = ""
    var lastname: String = ""
    var emailaddress1: String = ""
    private var birthdate: String = ""
    var idcrm_companycode: String = ""
    var verified: String = ""
    var statecode: String = ""
    var mobilephone: String = ""
    var idcrm_contactid: String = ""

    constructor(str: String) : this() {

        try {
            val jsonObject = JSONObject(str).getJSONObject("contact")

            this.firstname = jsonObject.getSafeString("firstname")
            this.lastname = jsonObject.getSafeString("lastname")
            this.emailaddress1 = jsonObject.getSafeString("emailaddress1")
            this.birthdate = jsonObject.getSafeString("birthdate")
            this.idcrm_companycode = jsonObject.getSafeString("idcrm_companycode")
            this.verified = jsonObject.getSafeString("verified")
            this.statecode = jsonObject.getSafeString("statecode")
            this.mobilephone = jsonObject.getSafeString("mobilephone")
            this.idcrm_contactid = jsonObject.getSafeString("idcrm_contactid")

        } catch (e: Exception) {

        }
    }

    fun updateNewContact(newContact: Contact) {
        this.firstname = newContact.firstname
        this.lastname = newContact.lastname
        this.birthdate = newContact.birthdate
        this.idcrm_companycode = newContact.idcrm_companycode
        this.mobilephone = newContact.mobilephone
    }

    fun updateNewContact(firstName: String, lastName: String, birthDate: String, companyCode: String, phone: String) {
        this.firstname = firstName
        this.lastname = lastName
        this.birthdate = birthDate
        this.idcrm_companycode = companyCode
        this.mobilephone = phone
    }

    fun getBirthDateDisplay(): String {

        if (birthdate == "") {
            return ""
        }

        var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        var finalSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        if (birthdate.length > 20)
            finalSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        var date = finalSimpleDateFormat.parse(birthdate)

        return simpleDateFormat.format(date)
    }

    fun getBirthDate(): Pair<String, Date> {

        if (birthdate == "") {
            return "" to Date()
        }

        var finalSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        if (birthdate.length > 20)
            finalSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        finalSimpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")

        return birthdate to finalSimpleDateFormat.parse(birthdate)
    }

    override fun toString(): String {
        return "first name:$firstname, lastname:$lastname, email:$emailaddress1, birthday:$birthdate, code:$idcrm_companycode, verified:$verified, statecode:$statecode" +
                "phone: $mobilephone, contact id:${idcrm_contactid}"
    }

}