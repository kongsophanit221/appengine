package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class Category : BaseItem {

    var id: String = ""
    var name: String = ""
    var products: ArrayList<Product> = ArrayList()
    var image: Annotation? = null //assign value in datasource
    var displayOrder: Int? = null

    val entityReference: EntityReference?
        get() = EntityReference(id, "idcrm_poscategory")

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        id = attribute["idcrm_poscategoryid"]!!.associatedValue.toString()
        name = attribute["idcrm_name"]!!.associatedValue.toString()
        displayOrder = attribute["idcrm_displayorder"]?.let {
            it.associatedValue as Int
        } ?: 99
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Category(attribute)
    }

    override fun toString(): String {
        return "id: $id, categoryName: $name + product: " + products.size
    }
}