package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class CartItem : BaseItem {

    var id: String = ""
    var product: Product? = null
    var quantity: Double = 0.0
    var description: String? = null
    var tax: Double = 0.0
    var discountAmount: Double = 0.0
    var pricePerUnit: Double = 0.0
    var amount: Double = 0.0
    var lineNumber: Int = 0

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute!!["idcrm_posorderlineid"]!!.associatedValue.toString()

        val productReferent = attribute!!["idcrm_productid"]!!.associatedValue as EntityReference
        this.product = Datasource.productsGlobal.filter { it.id == productReferent.id }.single()

        this.quantity = attribute!!["idcrm_quantity"]!!.associatedValue as Double
        this.lineNumber = attribute!!["idcrm_lineitemnumber"]!!.associatedValue as Int

        this.description = attribute!!["idcrm_name"]?.let {
            it.associatedValue.toString()
        }
        this.tax = attribute!!["idcrm_tax"]?.let {
            it.associatedValue as Double
        } ?: 0.0
        this.discountAmount = attribute!!["idcrm_discountamount"]?.let {
            it.associatedValue as Double
        } ?: 0.0
        this.pricePerUnit = attribute!!["idcrm_priceperunit"]!!.associatedValue as Double
        this.amount = attribute!!["idcrm_amount"]!!.associatedValue as Double
    }

    constructor(product: Product, quantity: Double, discount: Double = 0.0, tax: Double = 0.0) : this() {
        this.product = product
        this.quantity = quantity
        this.pricePerUnit = product.price
        this.amount = quantity * pricePerUnit
        this.discountAmount = amount * discount
        this.tax = amount * tax //the calculate of tax if discount amount or amount
    }

}