package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import com.soteca.loyaltyuserengine.app.Datasource
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import java.util.*

open class Order : BaseItem {

    open var id: String = ""
    open var totalTax: Double = 0.0
    open var totalDiscount: Double = 0.0
    open var totalItemAmount: Double = 0.0
    open var totalAmount: Double = 0.0
    open var venueName: String? = null
    open var venue: EntityReference? = null
    open var name: String? = null
    open var requestDeliverDate: Date? = null
    open var statuscode: Datasource.StatusReason? = null

    open var orderLines: ArrayList<OrderLine> = ArrayList()

    private var isMultiExe = false

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute["idcrm_posorderid"]!!.associatedValue.toString()
        this.venue = attribute["idcrm_venue"]?.associatedValue as? EntityReference
        this.name = attribute["idcrm_name"]!!.associatedValue.toString()

        this.totalItemAmount = attribute["idcrm_totalitemamount"]?.let { it.associatedValue as Double } ?: 0.0
        this.totalAmount = attribute["idcrm_totalamount"]?.let { it.associatedValue as Double } ?: 0.0
        this.totalTax = attribute["idcrm_totaltax"]?.let { it.associatedValue as Double } ?: 0.0
        this.totalDiscount = attribute["idcrm_totaldiscount"]?.let { it.associatedValue as Double } ?: 0.0
        this.requestDeliverDate = attribute["idcrm_requesteddeliverydate"]?.associatedValue as? Date
        this.statuscode = Datasource.StatusReason.from(attribute["statuscode"]!!.associatedValue.toString())
    }

    constructor(id: String = "", name: String? = null) {
        this.id = id
        this.name = name
    }

    open fun addExistCartItems(orderLines: ArrayList<OrderLine>) {
        this.orderLines.addAll(orderLines)
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Order(attribute)
    }

    val entityReference: EntityReference
        get() {
            return EntityReference(id = id, logicalName = "idcrm_posorder")
        }

    fun setMultiExecute(value: Boolean) {
        this.isMultiExe = value
    }

    override val attribute: EntityCollection.Attribute
        get() {
            val attr = EntityCollection.Attribute(arrayListOf())

            attr["idcrm_customerid"] = EntityCollection.ValueType.entityReference(EntityReference(User.current().contactId, "contact"))
            attr["idcrm_name"] = EntityCollection.ValueType.string("Order: ${User.current().contact.firstname + " " + User.current().contact.lastname}")

            if (id != "") {
                attr["idcrm_posorderid"] = EntityCollection.ValueType.guid(id)
            }

            attr["idcrm_totaltax"] = EntityCollection.ValueType.money(totalTax, isMultiExe)
            attr["idcrm_totaldiscount"] = EntityCollection.ValueType.money(totalDiscount, isMultiExe)
            attr["idcrm_totalitemamount"] = EntityCollection.ValueType.money(totalItemAmount, isMultiExe)
            attr["idcrm_totalamount"] = EntityCollection.ValueType.money(totalAmount, isMultiExe)

            venue?.let {
                attr["idcrm_venue"] = EntityCollection.ValueType.entityReference(it)
            }
            venueName?.let {
                attr["idcrm_venuename"] = EntityCollection.ValueType.string(it)
            }

            attr["statecode"] = EntityCollection.ValueType.optionSetValue(Datasource.StateCode.ACTIVE.value)
            attr["statuscode"] = EntityCollection.ValueType.optionSetValue(Datasource.StatusReason.OPEN.value)


            return attr
        }
}