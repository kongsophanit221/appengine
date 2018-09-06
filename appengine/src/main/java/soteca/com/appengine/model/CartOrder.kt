package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class CartOrder : Order {

    companion object {
        private var _shared: CartOrder? = null

        fun shared(): CartOrder {
            if (_shared == null) {
                _shared = CartOrder("")
            }
            return _shared!!
        }
    }

//    var status: String = "" //wait cyrille it first, stateCode

    constructor() {
        id = ""
        totalTax = 0.0
        totalDiscount = 0.0
        totalItemAmount = 0.0
        totalAmount = 0.0
        venue = null
        name = ""
        requestDeliverDate = null
        orderLines = ArrayList()
        statuscode = null
    }

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {

    }

    constructor(id: String, name: String = "") : super(id, name) {

    }

    override var totalTax: Double = 0.0
        get() {
            return orderLines.sumByDouble { it.tax }
        }

    override var totalDiscount: Double = 0.0
        get() {
            return orderLines.sumByDouble { it.discountAmount }
        }

    override var totalItemAmount: Double = 0.0
        get() {
            return orderLines.sumByDouble { it.totalPrice }
        }

    override var totalAmount: Double = 0.0
        get() = totalItemAmount - (totalDiscount + totalTax)

    val lastOrderLine: OrderLine?
        get() {
            val items = orderLines.sortedBy { it.lineNumber }
            return items.lastOrNull()
        }


    fun clear() {
        id = ""
        totalTax = 0.0
        totalDiscount = 0.0
        totalItemAmount = 0.0
        totalAmount = 0.0
        venue = null
        name = ""
        requestDeliverDate = null
        orderLines.clear()
        statuscode = null
    }

    fun replace(newCartOrder: CartOrder) {
        this.id = newCartOrder.id
        this.totalTax = newCartOrder.totalTax
        this.totalDiscount = newCartOrder.totalDiscount
        this.totalItemAmount = newCartOrder.totalItemAmount
        this.totalAmount = newCartOrder.totalAmount
        this.venue = null
        this.name = newCartOrder.name
        this.requestDeliverDate = null
        this.orderLines.clear()
        this.orderLines.addAll(newCartOrder.orderLines)
        this.statuscode = newCartOrder.statuscode
    }

    fun addCart(orderLine: OrderLine) {
        orderLines.add(orderLine)
    }

    fun removeCart(orderLine: OrderLine) {
        orderLines.remove(orderLine)
    }
}