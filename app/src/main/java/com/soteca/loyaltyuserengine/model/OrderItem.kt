package com.soteca.loyaltyuserengine.model

class OrderItem {

    var id: String? = null
    var isPreOrder: Boolean = false //After send pre order, the status change to true
    var cartItems: ArrayList<CartItem>? = null

}