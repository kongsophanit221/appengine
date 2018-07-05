package com.soteca.loyaltyuserengine.model

class CartItem() {

    var id: String = ""
    var product: Product? = null
    var quantity: Int = 0

    val amount: Double
        get() = product!!.price * quantity


    constructor(product: Product, quantity: Int) : this() {
        this.product = product
        this.quantity = quantity
    }

}