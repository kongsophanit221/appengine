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
    val customProductsSelect: HashMap<String, AuxiliaryProduct>
        get() {
            var tem = HashMap<String, AuxiliaryProduct>()
            customProducts?.let {
                it.forEach {
                    tem[it.key] = it.value.filter { it.isSelect == false }.single()
                }
            }
            return tem
        }

    var products: ArrayList<AuxiliaryProduct> = ArrayList()

    constructor() : super()

    constructor(attribute: EntityCollection.Attribute) : super(attribute)

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "bundle: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId + " " + auxiliaryProducts.size
    }

//    override fun clone(): BundleProduct {
//        val product = BundleProduct()
//
//        product.id = id
//        product.name = name
//        product.price = price
//        product.image = image
//        product.category = category
//        product.venue = venue
//        product.min = min
//        product.max = max
//        product.bundleId = bundleId
//
//        products.forEach {
//            product.products.add(it.clone())
//        }
//
//        auxiliaryProducts.forEach {
//            product.auxiliaryProducts.add(it.clone())
//        }
//
//        return product
//    }

    override fun clone(newProduct: Product?): Product {

        val product = BundleProduct()

        product.id = id
        product.name = name
        product.price = price
        product.image = image
        product.category = category
        product.venue = venue
        product.min = min
        product.max = max
        product.bundleId = bundleId

        products.forEach {
            product.products.add(it.clone() as AuxiliaryProduct)
        }

        auxiliaryProducts.forEach {
            product.auxiliaryProducts.add(it.clone() as AuxiliaryProduct)
        }

        return super.clone(product)
    }
}