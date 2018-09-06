package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import com.soteca.loyaltyuserengine.util.toStringDisplay
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class Card : BaseItem {

    enum class Type constructor(val value: String) {
        MEMBER("527210000"),
        GIFT("527210001"),
        VOUCHER("527210002");

        companion object {
            fun from(findValue: String): Type = Type.values().first { it.value == findValue }
        }
    }

    var id: String = ""
    var loyaltyProgram: EntityReference? = null
    var venueOfOrigin: EntityReference? = null
    var cardType: Type? = null
    private var totalPoints: Double? = null
    var contact: EntityReference? = null
    var transactionCurrency: EntityReference? = null
    var exchangeRate: Double? = null
    private var membershipLevel: EntityReference? = null
    var totalSpending: Double? = null
    var totalVisits: Double? = null
    var totalPointEarned: Double? = null
    var loyaltyUser: EntityReference? = null
    var companyProgram: EntityReference? = null

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute["idcrm_loyaltycardid"]?.associatedValue.toString()
        this.loyaltyProgram = attribute["idcrm_loyaltyprogram"]?.associatedValue as? EntityReference
        this.companyProgram = attribute["idcrm_companyprogram"]?.associatedValue as? EntityReference
        this.venueOfOrigin = attribute["idcrm_venueoforigin"]?.associatedValue as? EntityReference

        attribute["idcrm_typeofcard"]?.let {
            this.cardType = Type.from(it.associatedValue.toString())
        }

        this.totalPoints = attribute["idcrm_totalpoints"]?.associatedValue as? Double ?: 0.0
        this.contact = attribute["idcrm_contact"]?.associatedValue as? EntityReference
        this.transactionCurrency = attribute["transactioncurrencyid"]?.associatedValue as? EntityReference
        this.exchangeRate = attribute["exchangerate"]?.associatedValue as? Double
        this.membershipLevel = attribute["idcrm_membershiplevel"]?.associatedValue as? EntityReference
        this.totalSpending = attribute["idcrm_totalspendings"]?.associatedValue as? Double
        this.totalVisits = attribute["idcrm_totalvisits"]?.associatedValue as? Double
        this.totalPointEarned = attribute["idcrm_totalpointsearned"]?.associatedValue as? Double
        this.loyaltyUser = attribute["idcrm_loyaltyuser"]?.associatedValue as? EntityReference
    }

    fun getMembershipLevel(): String {
        membershipLevel?.let {
            return it.name?.toUpperCase()?.replace("LEVEL", "")!!
        } ?: return ""
    }

    fun getTotalPointString(): String {
        return totalPoints?.let {
            it.toStringDisplay()
        } ?: "0"
    }

    fun getTotalPoint(): Double {
        return totalPoints ?: 0.0
    }


    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Card(attribute)
    }
}