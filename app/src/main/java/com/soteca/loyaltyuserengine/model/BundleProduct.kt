package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class BundleProduct : Product {

    //    private var attribute: EntityCollection.Attribute? = null
    private var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList()
    private var customProducts: Map<String, List<AuxiliaryProduct>> = hashMapOf()
        get() {
            return auxiliaryProducts.groupBy {
                it!!.description!!
            }
        }

    var products: ArrayList<AuxiliaryProduct> = ArrayList()
    val customProductsSelect: HashMap<String, AuxiliaryProduct>
        get() {
            var tem = HashMap<String, AuxiliaryProduct>()
            customProducts?.let {
                it.forEach {
                    tem[it.key] = it.value.filter { it.isSelect == true }.single()
                }
            }
            return tem
        }

    constructor() : super()

    constructor(attribute: EntityCollection.Attribute) : super(attribute)

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "bundle: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId + " " + auxiliaryProducts.size
    }
}