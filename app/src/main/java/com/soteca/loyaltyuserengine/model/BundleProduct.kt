package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class BundleProduct(val attribute: EntityCollection.Attribute) : Product(attribute) {

    var products: ArrayList<Product>? = null
    private var customProducts: HashMap<String, ArrayList<SingleProduct>>? = null

    val customProductsSelect: HashMap<String, SingleProduct>
        get() {
            var tem = HashMap<String, SingleProduct>()
            customProducts?.let {
                it.forEach {
                    tem[it.key] = it.value.filter { it.isSelect == true }.single()
                }
            }
            return tem
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

    override fun toString(): String {
        return "bundle: " + id + " " + name + " " + price + " " + image + " " + category + " " + venue + " " + min + " " + max + " " + bundleId
    }
}