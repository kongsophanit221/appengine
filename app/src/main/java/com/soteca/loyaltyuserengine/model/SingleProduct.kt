package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class SingleProduct : Product {

    override val auxiliaryProductsAdd: ArrayList<AuxiliaryProduct>
        get() {
            var tem: ArrayList<AuxiliaryProduct> = ArrayList()
            tem.addAll(auxiliaryProducts.filter { it.isSelect == true })


            return tem
        }

    var isHasAuxiliary: Boolean = false
        get() = auxiliaryProducts.size > 0

    //    private var attribute: EntityCollection.Attribute? = null
//    override var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList()

    constructor() : super()

    constructor(attribute: EntityCollection.Attribute) : super(attribute)

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "product: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId + " " + auxiliaryProducts.size
    }

    override fun clone(newProduct: Product?): Product {

        val product = SingleProduct()

        product.id = id
        product.name = name
        product.price = price
        product.image = image
        product.category = category
        product.venue = venue
        product.min = min
        product.max = max
        product.bundleId = bundleId
        product.isAvailable = isAvailable

        auxiliaryProducts.forEach {
            product.auxiliaryProducts.add(it.clone() as AuxiliaryProduct)
        }

        return super.clone(product)
    }
}