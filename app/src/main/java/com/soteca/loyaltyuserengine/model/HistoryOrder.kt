package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import java.util.*
import kotlin.collections.ArrayList

class HistoryOrder : Order {

    var orderDate: Date? = null

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        orderDate = attribute!!["modifiedon"] as Date
    }

}
