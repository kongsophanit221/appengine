package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class BundleProduct : Product {

    private var attribute: EntityCollection.Attribute? = null
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

    override var category: String = ""
        get() = (attribute!!["idcrm_category"]!!.associatedValue as EntityReference).name!!

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
        return "bundle: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId + " " + auxiliaryProducts.size
    }
}