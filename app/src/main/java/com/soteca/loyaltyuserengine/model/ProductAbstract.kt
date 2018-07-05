package com.soteca.loyaltyuserengine.model

abstract class ProductAbstract() {

    abstract val id: String
        get
    abstract val name: String
        get
    abstract val price: Double
        get
    abstract val image: String
        get
    abstract val description: String
        get
    abstract val isChoose: Boolean?
        get
}