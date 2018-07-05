package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class BundleProduct() : Product() {

    private var _id: String = ""
    private var _name: String = ""
    private var _price: Double = 0.0
    private var _description: String? = null
    private var _image: String? = null


    private var products: ArrayList<Product>? = null
    private var customProducts: HashMap<String, ArrayList<Product>>? = null

    val customProductsSelect: HashMap<String, Product>
        get() {
            var tem = HashMap<String, Product>()
            customProducts?.let {
                it.forEach {
                    tem[it.key] = it.value.filter { it.isChoose == true }.single()
                }
            }
            return tem
        }

    constructor(attribute: EntityCollection.Attribute) : this() {
        _id = attribute!!["id"]!!.associatedValue as String
        _name = attribute!!["name"]!!.associatedValue as String
        _price = attribute!!["price"]!!.associatedValue as Double
        _description = attribute!!["description"]!!.associatedValue as String
        _image = attribute!!["image"]!!.associatedValue as String
    }

    override val id: String
        get() = _id
    override val name: String
        get() = _name
    override val price: Double
        get() = _price
    override val image: String
        get() = _image!!
    override val description: String
        get() = _description!!
    override val isChoose: Boolean?
        get() = null
}