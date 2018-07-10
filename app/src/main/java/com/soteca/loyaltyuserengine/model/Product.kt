package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

open class Product : BaseItem {

    constructor()

    constructor(data: EntityCollection.Attribute) : super(data)

    open var id: String = ""
    open var name: String = ""
    open var price: Double = 0.0
    open var image: String = ""
    open var category: String = ""
    open var venue: String = ""
    open var min: Double? = null
    open var max: Double? = null
    open var bundleId: String? = null

    override val attributePush: EntityCollection.Attribute?
        get() {
            val attr = EntityCollection.Attribute()
            attr["idcrm_posproductid"] = EntityCollection.ValueType.string(id)
            return attr
        }

}