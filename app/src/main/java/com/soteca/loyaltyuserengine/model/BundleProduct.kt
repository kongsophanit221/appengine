package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class BundleProduct(val attribute: EntityCollection.Attribute) : ProductAbstract(attribute) {

    private var _id: String = ""
    private var _name: String = ""
    private var _price: Double = 0.0
    private var _description: String? = null
    private var _image: String? = null


    private var products: ArrayList<ProductAbstract>? = null
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

    override val id: String
        get() = attribute!!["id"]!!.associatedValue as String
    override val name: String
        get() = attribute!!["name"]!!.associatedValue as String
    override val price: Double
        get() = attribute!!["price"]!!.associatedValue as Double
    override val image: String
        get() = attribute!!["image"]!!.associatedValue as String
    override val category: String
        get() = attribute!!["category"]!!.associatedValue as String
    override val venue: String
        get() = attribute!!["venue"]!!.associatedValue as String
    override val min: Double
        get() = attribute!!["min"]!!.associatedValue as Double
    override val max: Double
        get() = attribute!!["max"]!!.associatedValue as Double
}