package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

abstract class ProductAbstract(data: EntityCollection.Attribute) : BaseItem(data) {

    abstract val id: String
        get
    abstract val name: String
        get
    abstract val price: Double
        get
    abstract val image: String
        get
    abstract val isChoose: Boolean?
        get
}