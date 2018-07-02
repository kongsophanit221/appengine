package com.soteca.loyaltyuserengine

class ProductObject(
        var items: ArrayList<Products>? = null
)

class Products(
        var category: String? = null,
        var id: String? = null,
        var name: String? = null,
        var price: Float? = null,
        var qty: Int? = null,
        var image: String? = null,
        var size: String? = null,
        var description: String? = null,
        var timeAvaliable: String? = null,
        var promotion: String? = null,
        var recommended: String? = null
)