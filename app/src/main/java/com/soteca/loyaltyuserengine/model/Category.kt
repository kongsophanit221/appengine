package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection

class Category : BaseItem {

    var id: String = ""
    var name: String = ""
    var products: ArrayList<Product> = ArrayList()

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        id = attribute!!["idcrm_poscategoryid"]!!.associatedValue.toString()
        name = attribute!!["idcrm_name"]!!.associatedValue.toString()
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Category(attribute)
    }

    override fun toString(): String {
        return "id: $id, categoryName: $name + product: " + products.size
    }
}