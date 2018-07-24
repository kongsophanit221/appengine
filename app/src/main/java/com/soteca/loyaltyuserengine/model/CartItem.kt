package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.AliasedType
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

        var productReferent: EntityReference

        if (attribute["idcrm_productid"]!!.associatedValue is AliasedType) {
            productReferent = (attribute!!["idcrm_productid"]!!.associatedValue as AliasedType).value.associatedValue as EntityReference
            this.id = (attribute["idcrm_posorderlineid"]!!.associatedValue as AliasedType).value.associatedValue.toString()
            this.quantity = (attribute["idcrm_quantity"]!!.associatedValue as AliasedType).value.associatedValue as Double
            this.lineNumber = (attribute["idcrm_lineitemnumber"]!!.associatedValue as AliasedType).value.associatedValue as Int
            this.description = attribute["idcrm_name"]?.let { (it.associatedValue as AliasedType).value.associatedValue.toString() }
            this.tax = attribute["idcrm_tax"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.discountAmount = attribute["idcrm_discountamount"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.pricePerUnit = attribute["idcrm_priceperunit"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.amount = attribute["idcrm_amount"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0

        } else {
            productReferent = attribute!!["idcrm_productid"]!!.associatedValue as EntityReference
            this.id = attribute!!["idcrm_posorderlineid"]!!.associatedValue.toString()
            this.quantity = attribute!!["idcrm_quantity"]!!.associatedValue as Double
            this.lineNumber = attribute!!["idcrm_lineitemnumber"]!!.associatedValue as Int
            this.description = attribute!!["idcrm_name"]?.let { it.associatedValue.toString() }
            this.tax = attribute!!["idcrm_tax"]?.let { it.associatedValue as Double } ?: 0.0
            this.discountAmount = attribute!!["idcrm_discountamount"]?.let { it.associatedValue as Double } ?: 0.0
            this.pricePerUnit = attribute!!["idcrm_priceperunit"]!!.associatedValue as Double
            this.amount = attribute!!["idcrm_amount"]!!.associatedValue as Double
        }

        //this.product = Datasource.productsGlobal.find { it.id == productReferent.id }
    }

    constructor(product: Product, quantity: Double, discount: Double = 0.0, tax: Double = 0.0) : this() {
        this.product = product
        this.quantity = quantity
        this.pricePerUnit = product.price
        this.amount = quantity * pricePerUnit
        this.discountAmount = amount * discount
        this.tax = amount * tax //the calculate of tax if discount amount or amount
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return CartItem(attribute)
    }

}