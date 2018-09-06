package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection
import java.util.*

class HistoryOrder : Order {

    var orderDate: Date? = null

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        orderDate = attribute["modifiedon"]?.associatedValue as? Date
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return HistoryOrder(attribute)
    }

}
