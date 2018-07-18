package com.soteca.loyaltyuserengine.model

enum class DefaultEntity(val value: String) {
    ANNOTATION("annotation"),
    PRODUCT("idcrm_posproduct"),
    CATEGORY("idcrm_poscategory"),
    COMPONENT("idcrm_poscomponent");

    companion object {
        fun from(findValue: String): DefaultEntity = DefaultEntity.values().first { it.value == findValue }
    }

    val primaryIdAttribute: String
        get() {
            return when (this) {
                DefaultEntity.ANNOTATION -> "annotationid"
                DefaultEntity.PRODUCT -> "idcrm_posproductid"
                DefaultEntity.CATEGORY -> "idcrm_poscategoryid"
                DefaultEntity.COMPONENT -> "idcrm_poscomponentid"
            }
        }
}