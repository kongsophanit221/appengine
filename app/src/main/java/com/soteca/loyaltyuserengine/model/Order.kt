package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import java.util.*

open class Order : BaseItem {

    open var id: String? = ""
    open var totalTax: Double = 0.0
    open var totalDiscount: Double = 0.0
    open var totalItemAmount: Double = 0.0
    open var totalAmount: Double = 0.0
    open var venue: EntityReference? = null
    open var name: String = ""
    open var requestDeliverDate: Date? = null

    open var orderItems: ArrayList<CartItem> = ArrayList()

    constructor()

    constructor(name: String) {
        this.name = name
    }

    constructor(id: String, name: String) : this(name) {
        this.id = id
    }

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute!!["idcrm_posorderid"]!!.associatedValue.toString()
        this.venue = attribute!!["idcrm_venue"]?.associatedValue as? EntityReference
        this.name = attribute!!["idcrm_name"]!!.associatedValue.toString()

        this.totalItemAmount = attribute!!["idcrm_totalitemamount"]?.let { it.associatedValue as Double } ?: 0.0
        this.totalAmount = attribute!!["idcrm_totalamount"]?.let { it.associatedValue as Double } ?: 0.0
        this.totalTax = attribute!!["idcrm_totaltax"]?.let { it.associatedValue as Double } ?: 0.0
        this.totalDiscount = attribute!!["idcrm_totaldiscount"]?.let { it.associatedValue as Double } ?: 0.0
        this.requestDeliverDate = attribute!!["idcrm_requesteddeliverydate"]?.associatedValue as? Date
    }

    open fun addExistCartItems(cartItems: ArrayList<CartItem>) {
        orderItems.addAll(cartItems)
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Order(attribute)
    }

    val entityReference: EntityReference?
        get() {
            return EntityReference(id = id, logicalName = "idcrm_posorder")
        }

    open val keyValuePairs: EntityCollection.Attribute?
        get() {
            val attr = EntityCollection.Attribute(arrayListOf())
            attr["idcrm_name"] = EntityCollection.ValueType.string(name)
            return attr
        }

    var orderItemCount: Int = 0
        get() {
            return orderItems.count()
        }

}