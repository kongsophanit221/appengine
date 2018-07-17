package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import java.util.*

class HistoryOrder : Order {

    var attribute: EntityCollection.Attribute? = null

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.attribute = attribute
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return HistoryOrder(attribute)
    }

    var orderDate: Date? = null
        get() = attribute!!["idcrm_requesteddeliverydate"]!!.associatedValue as Date

    override fun toString(): String {
        return "OrderDate: $id, OrderId: $orderDate, totalTax: $totalTax"
    }

}
