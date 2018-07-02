package com.soteca.loyaltyuserengine.model

class CartItem : OrderItem {
    // TODO: field Product
    // TODO: field Package
    // TODO: constructor (product, quantity)
    // TODO: constructor (package, quantity)

    private var quantity: Int = 0

    constructor(quantity: Int) : super(quantity) {
        this.quantity = quantity
    }
}