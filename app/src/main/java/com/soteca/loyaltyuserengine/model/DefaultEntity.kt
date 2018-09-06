package com.soteca.loyaltyuserengine.model

class DefaultEntity {

    enum class DefaultEntityType(val value: String) {
        ANNOTATION("annotation"),
        PRODUCT("idcrm_posproduct"),
        CATEGORY("idcrm_poscategory"),
        COMPONENT("idcrm_poscomponent"),
        CARD("idcrm_loyaltycard"),
        CONTACT("contact"),
        ORDER("idcrm_posorder"),
        PROMOTION("idcrm_loyaltypromotion");

        companion object {
            fun from(findValue: String): DefaultEntityType = DefaultEntityType.values().first { it.value == findValue }
        }
    }

    private var type: DefaultEntityType? = null

    constructor(value: String) {
        type = DefaultEntityType.from(value)
    }

    val logicalName: String
        get() = type!!.value

    val primaryIdAttribute: String
        get() {
            return when (type) {
                DefaultEntityType.ANNOTATION -> "annotationid"
                DefaultEntityType.PRODUCT -> "idcrm_posproductid"
                DefaultEntityType.CATEGORY -> "idcrm_poscategoryid"
                DefaultEntityType.COMPONENT -> "idcrm_poscomponentid"
                DefaultEntityType.CARD -> "idcrm_loyaltycardid"
                DefaultEntityType.CONTACT -> "contactid"
                DefaultEntityType.ORDER -> "idcrm_posorderid"
                DefaultEntityType.PROMOTION -> "idcrm_loyaltypromotionid"
                else -> ""
            }
        }
}