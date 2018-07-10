package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class SingleProduct : ProductAbstract {

    private var attribute: EntityCollection.Attribute? = null
    private var auxiliaryProducts: ArrayList<AuxiliaryProduct>? = null //

    val auxiliaryProductsAdd: ArrayList<AuxiliaryProduct>
        get() {
            var tem: ArrayList<AuxiliaryProduct> = ArrayList()

            auxiliaryProducts?.let {
                tem.addAll(it.filter { it.isSelect == true })
            }

            return tem
        }

    var isSelect = false //for custom select product from category

    constructor() : super() {}

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.attribute = attribute
    }

    override val id: String
        get() = attribute!!["idcrm_posproductid"]!!.associatedValue.toString()
    override val name: String
        get() = attribute!!["idcrm_name"]!!.associatedValue.toString()
    override val price: Double
        get() = attribute!!["idcrm_pricesell"]!!.associatedValue as Double
    override val image: String
        get() = ""
    override val category: String
        get() {
            val catRef = attribute!!["idcrm_category"]!!.associatedValue as EntityReference
            return catRef.toString()
        }
    override val venue: String
        get() {
            val venueRef = attribute!!["idcrm_venue"]!!.associatedValue as EntityReference
            return venueRef.toString()
        }
    override val min: Double?
        get() = attribute!!["idcrm_min"]?.let {
            it.associatedValue as Double
        }

    override val max: Double?
        get() = attribute!!["idcrm_max"]?.let {
            it.associatedValue as Double
        }

    override fun toString(): String {
        return id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max
    }

}