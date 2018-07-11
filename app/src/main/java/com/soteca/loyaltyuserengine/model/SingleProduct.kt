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

    private var attribute: EntityCollection.Attribute? = null
    private var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList() //

    constructor() : super() {}

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.attribute = attribute
    }

    override var id: String = ""
        get() = attribute!!["idcrm_posproductid"]!!.associatedValue.toString()

    override var name: String = ""
        get() = attribute!!["idcrm_name"]!!.associatedValue.toString()

    override var price: Double = 0.0
        get() = attribute!!["idcrm_pricesell"]!!.associatedValue as Double

    override var image: String = ""

    override var category: EntityReference? = null
        get() = attribute!!["idcrm_category"]!!.associatedValue as EntityReference

    override var venue: String = ""
        get() = (attribute!!["idcrm_venue"]!!.associatedValue as EntityReference).name!!

    override var min: Double? = null
        get() = attribute!!["idcrm_min"]?.let {
            it.associatedValue as Double
        }

    override var max: Double? = null
        get() = attribute!!["idcrm_max"]?.let {
            it.associatedValue as Double
        }

    override var bundleId: String? = null
        get() = attribute!!["idcrm_bundle"]?.let {
            (it.associatedValue as EntityReference).id
        }

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "product: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId + " " + auxiliaryProducts.size
    }
}