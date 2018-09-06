package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import com.soteca.loyaltyuserengine.app.Datasource
import soteca.com.genisysandroid.framwork.model.AliasedType
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class OrderLine : BaseItem {

    var id: String = ""
    var quantity: Double = 0.0
        set(value) {
            field = value
            amount = pricePerUnit * value
        }
    var name: String? = null
    var tax: Double = 0.0
    var discountAmount: Double = 0.0
    var pricePerUnit: Double = 0.0
    var amount: Double = 0.0
    var lineNumber: Int = 0
    var orderReference: EntityReference? = null
    var productReference: EntityReference? = null
    var product: Product? = null
    var orderLinesChild: ArrayList<OrderLine> = ArrayList()

    val totalPrice: Double
        get() {
            return amount + orderLinesChild.sumByDouble { it.amount }
        }

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {

        if (attribute["idcrm_posorderlineid"]!!.associatedValue is AliasedType) {
            this.productReference = (attribute["idcrm_productid"]!!.associatedValue as AliasedType).value.associatedValue as EntityReference
            this.id = (attribute["idcrm_posorderlineid"]!!.associatedValue as AliasedType).value.associatedValue.toString()
            this.quantity = (attribute["idcrm_quantity"]?.associatedValue as AliasedType).value.associatedValue as Double
            this.lineNumber = (attribute["idcrm_lineitemnumber"]!!.associatedValue as AliasedType).value.associatedValue as Int
            this.name = attribute["idcrm_name"]?.let { (it.associatedValue as AliasedType).value.associatedValue.toString() }
            this.tax = 0.0//attribute["idcrm_tax"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.discountAmount = 0.0//attribute["idcrm_discountamount"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.pricePerUnit = attribute["idcrm_priceperunit"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.amount = attribute["idcrm_amount"]?.let { (it.associatedValue as AliasedType).value.associatedValue as Double } ?: 0.0
            this.orderReference = (attribute["idcrm_order"]!!.associatedValue as AliasedType).value.associatedValue as EntityReference

        } else {
            this.productReference = attribute["idcrm_productid"]!!.associatedValue as EntityReference
            this.id = attribute["idcrm_posorderlineid"]!!.associatedValue.toString()
            this.quantity = attribute["idcrm_quantity"]!!.associatedValue as Double
            this.lineNumber = attribute["idcrm_lineitemnumber"]!!.associatedValue as Int
            this.name = attribute["idcrm_name"]?.let { it.associatedValue.toString() }
            this.tax = 0.0//attribute!!["idcrm_tax"]?.let { it.associatedValue as Double } ?: 0.0
            this.discountAmount = 0.0//attribute!!["idcrm_discountamount"]?.let { it.associatedValue as Double } ?: 0.0
            this.pricePerUnit = attribute["idcrm_priceperunit"]!!.associatedValue as Double
            this.amount = attribute["idcrm_amount"]!!.associatedValue as Double
            this.orderReference = attribute["idcrm_order"]!!.associatedValue as EntityReference
        }
        this.product = Datasource.productsGlobal.find { this.productReference!!.id == it.id }?.clone()
    }

    constructor(product: Product, quantity: Double, cartOrder: CartOrder, lineNumber: Int = 0, discount: Double = 0.0, tax: Double = 0.0) : this() {
        this.product = product
        this.productReference = product.entityReference
        this.quantity = quantity
        this.pricePerUnit = product.price
        this.amount = quantity * pricePerUnit
        this.discountAmount = amount * discount
        this.tax = amount * tax //the calculate of tax if discount amount or amount
        this.lineNumber = lineNumber

        this.orderReference = cartOrder.entityReference
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return OrderLine(attribute)
    }

    val entityReference: EntityReference
        get() {
            return EntityReference(id = id, logicalName = "idcrm_posorderline")
        }


    override val attribute: EntityCollection.Attribute
        get() {
            val attr = EntityCollection.Attribute(arrayListOf())

            if (id != "")
                attr["idcrm_posorderlineid"] = EntityCollection.ValueType.guid(id)

            orderReference?.let { attr["idcrm_order"] = EntityCollection.ValueType.entityReference(it, true) }
            name?.let { attr["idcrm_name"] = EntityCollection.ValueType.string(it) }
            productReference?.let { attr["idcrm_productid"] = EntityCollection.ValueType.entityReference(it, true) }

            attr["idcrm_priceperunit"] = EntityCollection.ValueType.money(pricePerUnit, true)
            attr["idcrm_quantity"] = EntityCollection.ValueType.decimal(quantity)
            attr["idcrm_amount"] = EntityCollection.ValueType.money(amount, true)
            attr["idcrm_tax"] = EntityCollection.ValueType.money(tax, true)
            attr["idcrm_discountamount"] = EntityCollection.ValueType.money(discountAmount, true)
            attr["idcrm_lineitemnumber"] = EntityCollection.ValueType.int(lineNumber)

            return attr
        }

    fun autoPairOrderLineSelect() {

        when (product) {

            is BundleProduct -> {
                orderLinesChild.forEach { orderLine ->
                    (product as BundleProduct).auxiliaryProducts.filter { it.id == orderLine.product?.id }.forEach { it.isSelect = true }
                }
            }

            is SingleProduct -> {
                orderLinesChild.forEach { orderLine ->
                    (product as SingleProduct).auxiliaryProducts.filter { it.id == orderLine.product?.id }.forEach { it.isSelect = true }
                }
            }
        }
    }
}