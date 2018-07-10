package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection

abstract class ProductAbstract : BaseItem {

    constructor()

    constructor(data: EntityCollection.Attribute) : super(data)

    abstract val id: String
        get
    abstract val name: String
        get
    abstract val price: Double
        get
    abstract val image: String
        get
    abstract val category: String
        get
    abstract val venue: String
        get
    abstract val min: Double?
        get
    abstract val max: Double?
        get
}