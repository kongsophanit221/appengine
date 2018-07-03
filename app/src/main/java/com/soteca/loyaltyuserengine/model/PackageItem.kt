package com.soteca.loyaltyuserengine.model

class PackageItem {
    var id: String? = null
    var name: String? = null
    var price: Float = 0.0f
    var products: ArrayList<ProductItem>? = null
    var customProducts: ArrayList<ProductItem>? = null // when first initialize, productItem.isSelect = false
}