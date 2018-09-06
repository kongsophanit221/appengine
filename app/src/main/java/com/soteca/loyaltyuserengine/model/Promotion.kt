package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import java.util.*

class Promotion : BaseItem {

    var id: String = ""
    var promotionName: String = ""
    var validFrom: Date? = null
    var validUntil: Date? = null
    var shortTitle: String? = null
    var discount: Double? = null
    var loyaltyProgram: EntityReference? = null
    var image: Annotation? = null
    var description: String? = null

    var isCompanyProgram = false

    val entityReference: EntityReference?
        get() = EntityReference(id, "idcrm_loyaltypromotion")

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute["idcrm_loyaltypromotionid"]?.associatedValue.toString()
        this.promotionName = attribute["idcrm_promotionname"]?.associatedValue.toString()
        this.validFrom = attribute["idcrm_validfrom"]?.associatedValue as? Date
        this.validUntil = attribute["idcrm_validuntil"]?.associatedValue as? Date
        this.shortTitle = attribute["idcrm_shorttitle"]?.associatedValue as? String
        this.discount = attribute["idcrm_discountpercentage"]?.associatedValue as? Double
        this.loyaltyProgram = attribute["idcrm_loyaltyprogram"]?.associatedValue as? EntityReference
        this.description = attribute["idcrm_description"]?.associatedValue as? String
    }

    fun toCompanyProgram() {
        this.isCompanyProgram = true
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Promotion(attribute)
    }
}