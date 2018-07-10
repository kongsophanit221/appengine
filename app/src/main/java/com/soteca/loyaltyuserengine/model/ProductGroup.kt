package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class ProductGroup : BaseItem {

    private var attribute: EntityCollection.Attribute? = null

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.attribute = attribute
    }

    var id: String = ""
        get() = attribute!!["idcrm_poscategoryid"]!!.associatedValue.toString()
    var categoryName: String = ""
        get() = attribute!!["idcrm_name"]!!.associatedValue.toString()
    var image: String = ""
        get() = ""

    override fun toString(): String {
        return "id: $id, categoryName: $categoryName"
    }
}