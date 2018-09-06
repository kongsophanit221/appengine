package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

/**
Relationship between Product n Product itself (N to N)
1 Single Product can have many auxiliary products n 1 auxiliary product have many single product
1 Bundle Product can have many single products n 1 single product have many bundle product
 */
class Component() : BaseItem() {
    var id: String = ""
    var name: String = ""
    var productId: String = ""
    var applyToId: String = ""

    constructor(attribute: EntityCollection.Attribute) : this() {
        id = attribute["idcrm_poscomponentid"]!!.associatedValue.toString()
        name = attribute["idcrm_name"]!!.associatedValue.toString()
        productId = (attribute["idcrm_product"]!!.associatedValue as EntityReference).id!!
        applyToId = (attribute["idcrm_applyto"]!!.associatedValue as EntityReference).id!!
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Component(attribute)
    }

    override fun toString(): String {
        return id + " " + name + " " + productId + " " + applyToId
    }
}