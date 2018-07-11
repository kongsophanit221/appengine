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

    //    private var attribute: EntityCollection.Attribute? = null
    private var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList() //
    private var _id: String = ""
    private var _name: String = ""
    private var _image: String = ""
    private var _category: String = ""
    private var _venus: String = ""
    private var _min: Double? = null
    private var _max: Double? = null
    private var _bundleId: String? = null

    constructor() : super() {}

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this._id = attribute!!["idcrm_posproductid"]!!.associatedValue.toString()
        this._name = attribute!!["idcrm_name"]!!.associatedValue.toString()
        this._venus = (attribute!!["idcrm_venue"]!!.associatedValue as EntityReference).name!!
        this._category = (attribute!!["idcrm_category"]!!.associatedValue as EntityReference).name!!

        this._min = attribute!!["idcrm_min"]?.let {
            it.associatedValue as Double
        }
        this._max = attribute!!["idcrm_max"]?.let {
            it.associatedValue as Double
        }
        this._bundleId = attribute!!["idcrm_bundle"]?.let {
            (it!!.associatedValue as EntityReference).id
        }

    }

    constructor(newAuxiliaryProduct: AuxiliaryProduct, description: String?) : this() {
        this._id = newAuxiliaryProduct.id
        this._name = newAuxiliaryProduct.name
        this._venus = newAuxiliaryProduct.venue
        this._bundleId = newAuxiliaryProduct.bundleId
        this._category = newAuxiliaryProduct.category
        this._min = newAuxiliaryProduct.min
        this._max = newAuxiliaryProduct.max
        this.description = description
    }

    override var id: String = ""
        get() = _id

    override var name: String = ""
        get() = _name

    override var price: Double = 0.0

    override var image: String = ""

    override var category: String = ""
        get() = _category

    override var venue: String = ""
        get() = _venus

    override var min: Double? = null
        get() = _min

    override var max: Double? = null
        get() = _max

    override var bundleId: String? = null
        get() = _bundleId

    override fun addOnComponent(product: AuxiliaryProduct) {
        auxiliaryProducts.add(product)
    }

    override fun toString(): String {
        return "auxiliary id: " + id + " name: " + _name + " bundleId: " + _bundleId
    }
}