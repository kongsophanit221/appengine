package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import java.util.*

class CartOrder() : Order() {

    private var _id: String = ""
    private var _orderItems: ArrayList<CartItem>? = null

    constructor(attribute: EntityCollection.Attribute) : this() {
        this._id = attribute["id"]!!.associatedValue as String
        // order Item
    }

    override val id: String
        get() = _id

    override val orderItems: ArrayList<CartItem>
        get() = _orderItems.let {
            return it!!
        } ?: ArrayList()

    override val isCanChange: Boolean
        get() = true
}