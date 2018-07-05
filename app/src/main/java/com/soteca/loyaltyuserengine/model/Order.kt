package com.soteca.loyaltyuserengine.model

abstract class Order {
    abstract val id: String
        get
    abstract val orderItems: ArrayList<CartItem>
        get
    abstract val isCanChange: Boolean
        get
}