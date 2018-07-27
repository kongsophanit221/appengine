package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class CartItem : BaseItem {

    var id: String = ""
    var product: Product? = Product()
    var quantity: Double = 0.0
    var description: String? = null
    var tax: Double = 0.0
    var discountAmount: Double = 0.0
    var pricePerUnit: Double = 0.0
    var amount: Double = 0.0
    var lineNumber: Int = 0
    var name: String = ""
    var productId: String = ""

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute!!["idcrm_posorderlineid"]!!.associatedValue.toString()

        val productReferent = attribute!!["idcrm_productid"]!!.associatedValue as EntityReference
        //this.product = Datasource.productsGlobal.find { it.id == productReferent.id }

        this.quantity = attribute!!["idcrm_quantity"]!!.associatedValue as Double
        this.lineNumber = attribute!!["idcrm_lineitemnumber"]!!.associatedValue as Int

        this.description = attribute!!["idcrm_name"]?.let { it.associatedValue.toString() }
        this.tax = attribute!!["idcrm_tax"]?.let { it.associatedValue as Double } ?: 0.0
        this.discountAmount = attribute!!["idcrm_discountamount"]?.let { it.associatedValue as Double } ?: 0.0
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

    constructor(name: String, productId: String) {
        this.name = name
        this.productId = productId
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return CartItem(attribute)
    }

    val entityReference: EntityReference?
        get() {
            return EntityReference(id = id, logicalName = "idcrm_posorderline")
        }

    val productReference: EntityReference?
        get() {
            return EntityReference(id = productId, logicalName = "idcrm_posproduct")
        }

    val keyValuePairs: EntityCollection.Attribute?
        get() {
            val attr = EntityCollection.Attribute(arrayListOf())
            attr["idcrm_name"] = EntityCollection.ValueType.string(name)
            attr["idcrm_productid"] = EntityCollection.ValueType.entityReference(productReference!!)
            return attr
        }

}