package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class SingleProduct : Product {

    val auxiliaryProductsAdd: ArrayList<AuxiliaryProduct>
        get() {
            var tem: ArrayList<AuxiliaryProduct> = ArrayList()

            auxiliaryProducts?.let {
                tem.addAll(it.filter { it.isSelect == true })
            }

            return tem
        }

    var isHasAuxiliary: Boolean = false
        get() = auxiliaryProducts.size > 0

    //    private var attribute: EntityCollection.Attribute? = null
    private var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList()

    constructor() : super()

    constructor(attribute: EntityCollection.Attribute) : super(attribute)

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "product: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId + " " + auxiliaryProducts.size
    }
}