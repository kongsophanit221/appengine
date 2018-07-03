package com.soteca.loyaltyuserengine.model

class CartItem() {

    var product: ProductItem? = null
    var package_: PackageItem? = null
    var quantity: Int = 0

    val amount: Float? //auto calculate
        get() {
            product?.let {
                return product!!.price * quantity
            } ?: run {
                return package_!!.price * quantity
            }
        }

    val name: String?
        get() {
            product?.let {
                return product!!.name
            } ?: run {
                return package_!!.name
            }
        }

    constructor(product: ProductItem, quantity: Int) : this() {
        this.product = product
        this.quantity = quantity
    }

    constructor(package_: PackageItem, quantity: Int) : this() {
        this.package_ = package_
        this.quantity = quantity
    }
}