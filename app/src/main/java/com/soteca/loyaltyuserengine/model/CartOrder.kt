package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class CartOrder : Order {

    companion object {
        private var _shared: CartOrder? = null

        fun shared(attribute: EntityCollection.Attribute? = null): CartOrder? {
            if (_shared == null && attribute != null) {
                _shared = CartOrder(attribute!!)
            }
            return _shared!!
        }
    }

    var status: String = "" //wait cyrille it first, stateCode

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        _shared = this
    }

    constructor(id: String, name: String = "") : super(id, name) {
        _shared = this
    }

    override var totalTax: Double = 0.0
        get() {
            return orderItems.sumByDouble { it.tax }
        }

    override var totalDiscount: Double = 0.0
        get() {
            return orderItems.sumByDouble { it.discountAmount }
        }

    override var totalItemAmount: Double = 0.0
        get() {
            return orderItems.sumByDouble { it.amount }
        }

    override var totalAmount: Double = 0.0
        get() = totalItemAmount - totalDiscount + totalTax

    fun clear() {
        id = ""
        totalTax = 0.0
        totalDiscount = 0.0
        totalItemAmount = 0.0
        totalAmount = 0.0
        venue = null
        name = ""
        requestDeliverDate = null
        orderItems = ArrayList()
    }

    fun addCart(orderLine: OrderLine) {
        orderItems.add(orderLine)
    }

    fun removeCart(orderLine: OrderLine) {
        orderItems.remove(orderLine)
    }
}