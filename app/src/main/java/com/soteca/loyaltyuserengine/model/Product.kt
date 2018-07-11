package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

open class Product : BaseItem {

    constructor()

    constructor(data: EntityCollection.Attribute) : super(data)

    open var id: String = ""
    open var name: String = ""
    open var price: Double = 0.0
    open var image: String = ""
    open var category: EntityReference? = null
    open var venue: String = ""
    open var min: Double? = null
    open var max: Double? = null
    open var bundleId: String? = null

    val entityReference: EntityReference?
        get() = EntityReference(id, "idcrm_posproduct")

    open fun addOnComponent(product: AuxiliaryProduct) {}

    override val attributePush: EntityCollection.Attribute?
        get() {
            val attr = EntityCollection.Attribute()
            attr["idcrm_posproductid"] = EntityCollection.ValueType.string(id)
            return attr
        }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        val isBundle = attribute!!["idcrm_isbundle"]!!.associatedValue as Boolean
        val isBelongToBundle = attribute!!["idcrm_belongstobundle"]!!.associatedValue as Boolean
        val isAuxiliary = attribute!!["idcrm_isauxiliary"]!!.associatedValue as Boolean

        if (isBundle) {
            return BundleProduct(attribute!!)
        } else if (!isBelongToBundle && !isAuxiliary) {
            return SingleProduct(attribute!!)
        } else {
            return AuxiliaryProduct(attribute!!)
        }
    }

}