package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class AuxiliaryProduct : Product {

    val auxiliaryProductsAdd: ArrayList<AuxiliaryProduct>
        get() {
            var tem: ArrayList<AuxiliaryProduct> = ArrayList()

            auxiliaryProducts?.let {
                tem.addAll(it.filter { it.isSelect == true })
            }

            return tem
        }

    var description: String? = null // The Name of POC Component Entity
    var isSelect = false //for custom select product auxiliary from product
    var isHasAuxiliary: Boolean = false
        get() = auxiliaryProducts.size > 0

    private var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList()

    constructor() : super()

    constructor(attribute: EntityCollection.Attribute) : super(attribute)

    constructor(newAuxiliaryProduct: AuxiliaryProduct, description: String?) : this() {
        this.id = newAuxiliaryProduct.id
        this.name = newAuxiliaryProduct.name
        this.venue = newAuxiliaryProduct.venue
        this.bundleId = newAuxiliaryProduct.bundleId
        this.category = newAuxiliaryProduct.category
        this.price = newAuxiliaryProduct.price
        this.min = newAuxiliaryProduct.min
        this.max = newAuxiliaryProduct.max
        this.description = description
    }

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "auxiliary id: " + id + " name: " + name + " bundleId: " + bundleId
    }
}