package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import java.util.*
import kotlin.collections.ArrayList

class HistoryOrder() : OrderAbstract() {

    private var _id: String = ""
    var date: Date? = null
    private var _orderItems: ArrayList<CartItem>? = null

    constructor(attribute: EntityCollection.Attribute) : this() {
        this._id = attribute["id"]!!.associatedValue as String
        this.date = attribute["date"]!!.associatedValue as Date
        // order Item
    }

    override val id: String
        get() = _id

    override val orderItems: ArrayList<CartItem>
        get() = _orderItems!!

    override val isCanChange: Boolean
        get() = false

}
