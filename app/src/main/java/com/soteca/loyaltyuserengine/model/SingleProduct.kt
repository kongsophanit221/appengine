package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

class SingleProduct(attribute: EntityCollection.Attribute) : ProductAbstract(attribute) {

    private var _id: String = ""
    private var _name: String = ""
    private var _price: Double = 0.0
    private var _image: String? = null

    private var _category: String? = null
    private var _referennce: String? = null
    private var _venue: String? = null
    private var _min: Double? = null
    private var _max: Double? = null

    private var auxiliaryProducts: ArrayList<AuxiliaryProduct>? = null //

    val auxiliaryProductsAdd: ArrayList<AuxiliaryProduct>
        get() {
            var tem: ArrayList<AuxiliaryProduct> = ArrayList()

            auxiliaryProducts?.let {
                tem.addAll(it.filter { it.isSelect == true })
            }

            return tem
        }

//    constructor(attribute: EntityCollection.Attribute) : this() {
//        _id = attribute!!["id"]!!.associatedValue as String
//        _name = attribute!!["name"]!!.associatedValue as String
//        _price = attribute!!["price"]!!.associatedValue as Double
//        _image = attribute!!["image"]!!.associatedValue as String
//    }

    override val id: String
        get() = _id
    override val name: String
        get() = _name
    override val price: Double
        get() = _price
    override val image: String
        get() = _image!!
    override val isChoose: Boolean?
        get() = true
}