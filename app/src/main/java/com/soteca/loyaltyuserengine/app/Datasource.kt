package com.soteca.loyaltyuserengine.app

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebConfig
import com.soteca.loyaltyuserengine.model.*
import com.soteca.loyaltyuserengine.model.Annotation
import com.soteca.loyaltyuserengine.util.ImageScaleType
import com.soteca.loyaltyuserengine.util.SharedPreferenceUtils
import com.soteca.loyaltyuserengine.util.getSafeString
import org.json.JSONObject
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.model.encoder.body.ActionRequest
import soteca.com.genisysandroid.framwork.networking.Errors

class Datasource {

    enum class StateCode(val value: String) {
        ACTIVE("0"),
        INACTIVE("1");
    }

    enum class StatusReason(val value: String) {
        OPEN("527210000"),
        COMPLETE("527210001"),
        CANCEL("527210002");
    }

    companion object {

        var productsGlobal: ArrayList<Product> = ArrayList()

        private var _shared: Datasource? = null

        fun shared(context: Context): Datasource {
            if (_shared == null) {
                _shared = Datasource(context)
            }
            return _shared!!
        }
    }

    private var appData: AppDatasourceImp
    private var context: Context

    constructor(context: Context) {
        this.appData = AppDatasourceImp(context)
        this.context = context
    }

    fun getCategaries(handler: (ArrayList<Category>?, Error?) -> Unit) {

        val attributebutes = arrayListOf(
                "idcrm_poscategoryid",
                "idcrm_name"
        )

        val expression = FetchExpression.fetct(entityType = "idcrm_poscategory", attributes = attributebutes, filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))

        appData.getMultiple(Category(), expression) { categories: ArrayList<Category>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            handler(categories, null)
        }
    }

    fun getCategariesComplete(handler: (ArrayList<Category>?, Error?) -> Unit) {

        val attributes = arrayListOf(
                "idcrm_poscategoryid",
                "idcrm_name"
        )
        val expression = FetchExpression.fetct(entityType = "idcrm_poscategory", attributes = attributes, filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))

        getCategaries { categories, errors ->

            getProducts({ products, error ->

                categories!!.forEach {
                    it.products.addAll(products!!.filter { pit -> pit.category!!.id == it.id })
                }

                handler(categories, null)
            })

        }

        appData.getMultiple(Category(), expression) { categories: ArrayList<Category>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            getProducts({ products, error ->

                categories!!.forEach {
                    it.products.addAll(products!!.filter { pit -> pit.category!!.id == it.id })
                }

                handler(categories, null)
            })

        }

    }

    fun getProducts(handler: (ArrayList<Product>?, Error?) -> Unit) {

        val defaultEntity = DefaultEntity("idcrm_posproduct")

        val attributes = arrayListOf(
                FetchExpression.Attributee("idcrm_posproductid"),
                FetchExpression.Attributee("idcrm_name"),
                FetchExpression.Attributee("idcrm_pricesell"),
                FetchExpression.Attributee("idcrm_category"),
                FetchExpression.Attributee("idcrm_venue"),
                FetchExpression.Attributee("idcrm_min"),
                FetchExpression.Attributee("idcrm_max"),
                FetchExpression.Attributee("idcrm_bundle"),
                FetchExpression.Attributee("idcrm_isbundle"),
                FetchExpression.Attributee("idcrm_belongstobundle"),
                FetchExpression.Attributee("idcrm_isauxiliary")
        )

        val attributesAnnotation = arrayListOf(
                FetchExpression.Attributee("annotationid"),
                FetchExpression.Attributee("filename"),
                FetchExpression.Attributee("objectid"),
                FetchExpression.Attributee("documentbody"),
                FetchExpression.Attributee("mimetype")
        )

        val alias = "productImage"

        val imageCondition = FetchExpression.Condition(attribute = "subject", operator = FetchExpression.Operator.like, value = "%${ImageScaleType.SMALL.subjectName()}%")
        val linkEntity = FetchExpression.LinkEntity(name = "annotation", from = "objectid", to = defaultEntity.primaryIdAttribute, linkType = FetchExpression.LinkType.OUTER, alias = alias, attributes = attributesAnnotation, filter = FetchExpression.Filter.singleCondition(imageCondition))
        val entity = FetchExpression.Entity(name = defaultEntity.logicalName, attributes = attributes, linkEntities = arrayListOf(linkEntity), filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)))

        val expression = FetchExpression(entity = entity)

        appData.getMultiple(Product(), alias, expression, { products, annotations, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            products!!.forEach {
                it.image = annotations?.find { an -> an.objectReference!!.id == it.id }
            }

            productsGlobal.addAll(products!!)

            handler(products, null)
        })
    }

    fun getComponents(handler: (ArrayList<Component>?, Error?) -> Unit) {

        val attributes = arrayListOf(
                "idcrm_poscomponentid",
                "idcrm_name",
                "idcrm_product",
                "idcrm_applyto"
        )
        val expression = FetchExpression.fetct(entityType = "idcrm_poscomponent", attributes = attributes, filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)))

        appData.getMultiple(Component(), expression, { components: ArrayList<Component>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }
            handler(components, null)
        })
    }

    fun getProductsComplete(handler: (ArrayList<Product>?, Error?) -> Unit) {

        getProducts { products, error ->

            if (error != null) {
                handler(null, error)
                return@getProducts
            }

            getComponents { components, error ->

                if (error != null) {
                    handler(null, error)
                    return@getComponents
                }

                // Getting Auxiliary product as AddOn put in Single and Auxiliary, as Custom product put in Bundle
                components!!.forEach {
                    val auxiliary = products!!.find { pit -> pit.id == it.productId } as AuxiliaryProduct
                    val applyTo = products!!.find { ait -> ait.id == it.applyToId }
                    applyTo?.addOnComponent(AuxiliaryProduct(auxiliary, it.name))
                }

                // Getting Auxiliary product as Single product put in to list in Bundle product
                var bundleProducts = products!!.filter { it is BundleProduct }
                bundleProducts.forEach {
                    val bundelProduct = (it as BundleProduct)
                    bundelProduct.products.addAll(products!!.filter { it.bundleId == bundelProduct.id }.map { it as AuxiliaryProduct })
                }

                var finalProducts: ArrayList<Product> = arrayListOf()
                finalProducts.addAll(products!!.filter { it is SingleProduct || it is BundleProduct })

                handler(finalProducts, null)
            }
        }
    }

    fun getLatestOrder(handler: (HistoryOrder?, Error?) -> Unit) {

        val customerId = SharedPreferenceUtils.shared(context).getUserId()
        if (customerId == "") {
            handler(null, Error("Customer Id is empty"))
            return
        }

        val attributesOrder = arrayListOf(
                FetchExpression.Attributee("idcrm_posorderid"),
                FetchExpression.Attributee("idcrm_venue"),
                FetchExpression.Attributee("idcrm_name"),
                FetchExpression.Attributee("idcrm_totalitemamount"),
                FetchExpression.Attributee("idcrm_totalamount"),
                FetchExpression.Attributee("idcrm_totaltax"),
                FetchExpression.Attributee("idcrm_totaldiscount"),
                FetchExpression.Attributee("idcrm_requesteddeliverydate"),
                FetchExpression.Attributee("modifiedon")
        )
        val customerCondition = FetchExpression.Condition(attribute = "idcrm_customerid", operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = customerId, uiType = "contact"))
        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val statusReasonCondition = FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = StatusReason.COMPLETE.value)
        val conditions = arrayListOf(stateCodeCondition, statusReasonCondition, customerCondition)
        val filters = FetchExpression.Filter.andConditions(conditions)
        val orders = arrayListOf(FetchExpression.Order(attribute = "modifiedon", descending = true))

        val entity = FetchExpression.Entity(name = "idcrm_posorder", attributes = attributesOrder, filter = filters, orders = orders)
        val expression = FetchExpression(entity = entity, top = 1)

        appData.getMultiple(HistoryOrder(), expression) { historyOrders: ArrayList<HistoryOrder>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }
            val historyOrder: HistoryOrder? = historyOrders?.let { it.single() }

            historyOrder?.let {

                getOrderLines(it) { orderLines: ArrayList<OrderLine>?, error: Error? ->

                    it.addExistCartItems(orderLines!!)
                    handler(it, error)
                }
            } ?: run {
                handler(null, null)
            }
        }
    }

    fun getExistedOrders(handler: (CartOrder?, Error?) -> Unit) {

        val customerId = SharedPreferenceUtils.shared(context).getUserId()
        if (customerId == "") {
            handler(null, Error("Customer Id is empty"))
            return
        }

        val alias = "orderItem"

        val attributesOrder = arrayListOf(
                FetchExpression.Attributee("idcrm_posorderid"),
                FetchExpression.Attributee("idcrm_venue"),
                FetchExpression.Attributee("idcrm_name"),
                FetchExpression.Attributee("idcrm_totalitemamount"),
                FetchExpression.Attributee("idcrm_totalamount"),
                FetchExpression.Attributee("idcrm_totaltax"),
                FetchExpression.Attributee("idcrm_totaldiscount"),
                FetchExpression.Attributee("idcrm_requesteddeliverydate"),
                FetchExpression.Attributee("modifiedon")
        )

        val customerCondition = FetchExpression.Condition(attribute = "idcrm_customerid", operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = customerId, uiType = "contact"))
        val orderItemLinkEntity = FetchExpression.LinkEntity(name = "idcrm_posorderline", from = "idcrm_order", to = "idcrm_posorderid", alias = alias, linkType = FetchExpression.LinkType.OUTER)
        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val statusReasonCondition = FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = StatusReason.OPEN.value)
        val filter = FetchExpression.Filter.andConditions(arrayListOf(stateCodeCondition, statusReasonCondition, customerCondition))
        val order = FetchExpression.Order(attribute = "modifiedon", descending = true)
        val entity = FetchExpression.Entity(name = "idcrm_posorder", attributes = attributesOrder, linkEntities = arrayListOf(orderItemLinkEntity), filter = filter, orders = arrayListOf(order))
        val expression = FetchExpression(entity)

        DynamicsConnector.default(context).retrieveMultiple(expression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@retrieveMultiple
            }

            val finalAlias = alias + "."
            var cartItems = ArrayList<OrderLine>()
            var cartOrder: CartOrder? = null

            entityCollection!!.entityList?.first()?.let {
                val cardOrderAttr = ArrayList<EntityCollection.KeyValuePairOfstringanyType>()
                cardOrderAttr.addAll(it.attribute!!.keyValuePairList!!.filter { kv -> !kv.key!!.contains(finalAlias) })
                cartOrder = CartOrder(EntityCollection.Attribute(cardOrderAttr))
            }

            entityCollection!!.entityList?.forEach {
                val cardItemsList = it.attribute!!.keyValuePairList!!.filter { it.key!!.contains(finalAlias) }
                val cardItemsAttr = ArrayList<EntityCollection.KeyValuePairOfstringanyType>()
                cardItemsAttr.addAll(cardItemsList)

                cardItemsAttr.forEach {
                    it.key = it.key!!.replace(finalAlias, "")
                }
                cartItems.add(OrderLine(EntityCollection.Attribute(cardItemsAttr)))
            }

            cartOrder!!.addExistCartItems(cartItems)

            handler(cartOrder, null)
        }
    }

    fun getOrderLines(order: Order, handler: (ArrayList<OrderLine>?, Error?) -> Unit) {

        val attributesCart = arrayListOf(
                "idcrm_posorderlineid",
                "idcrm_productid",
                "idcrm_quantity",
                "idcrm_lineitemnumber",
                "idcrm_name",
                "idcrm_tax",
                "idcrm_discountamount",
                "idcrm_priceperunit",
                "idcrm_amount"
        )

        val orderCondition = FetchExpression.Condition(attribute = "idcrm_order", operator = FetchExpression.Operator.equal, value = order.id)
        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val expression = FetchExpression.fetct(entityType = "idcrm_posorderline", attributes = attributesCart, filter = FetchExpression.Filter.andConditions(arrayListOf(orderCondition, stateCodeCondition)))

        appData.getMultiple(OrderLine(), expression) { orderLines: ArrayList<OrderLine>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }
            handler(orderLines, errors)
        }
    }

    fun deleteOrderLine(orderLine: OrderLine, handler: (Boolean?, Error?) -> Unit) {

        appData.delete("idcrm_posorderline", orderLine.id, { status, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@delete
            }

            handler(status, null)
        })
    }

    fun cancelOrder(order: CartOrder, handler: (Boolean?, Error?) -> Unit) {

        val keyValuePairStatusCode = EntityCollection.KeyValuePairOfstringanyType(key = "statuscode", valueType = EntityCollection.Value(EntityCollection.ValueType.optionSetValue(value = StatusReason.CANCEL.value)))
        val entity = EntityCollection.Entity(id = order.id, logicalName = "idcrm_posorder", attribute = EntityCollection.Attribute(keyValuePairList = arrayListOf(keyValuePairStatusCode)))

        appData.update(entity) { status, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@update
            }

            handler(status, null)
        }
    }

    fun createOrder(order: CartOrder, handler: (CartOrder?, Error?) -> Unit) {

        val customerId = SharedPreferenceUtils.shared(context).getUserId()
        if (customerId == "") {
            handler(null, Error("Customer Id is empty"))
            return
        }

        val attribute = order.attribute
        attribute["idcrm_customerid"] = EntityCollection.ValueType.entityReference(EntityReference(customerId, "contact"))
        attribute["statecode"] = EntityCollection.ValueType.optionSetValue(StateCode.ACTIVE.value)
        attribute["statuscode"] = EntityCollection.ValueType.optionSetValue(StatusReason.OPEN.value)

        val entity = EntityCollection.Entity(attribute = attribute, logicalName = order.entityReference.logicalName)

        appData.create(entity) { id, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@create
            }

            order.id = id!!

            handler(order, null)
        }
    }

    private fun createOrderLines(order: CartOrder, product: Product, quantity: Double, discount: Double = 0.0): ArrayList<OrderLine> {

        val cartOrderEntityReference = null//order.entityReference
        var existLine = 0 //order.orderItems!!.size
        var cartItems = ArrayList<OrderLine>()

        existLine = existLine.plus(1)
        cartItems.add(OrderLine(product, quantity, cartOrderEntityReference, existLine, discount = discount))

        when (product) {
            is SingleProduct -> {
                product.auxiliaryProductsAdd.forEach {
                    existLine = existLine.plus(1)
                    cartItems.add(OrderLine(it, 1.0, cartOrderEntityReference, existLine))
                }
            }
            is BundleProduct -> {
                product.products.forEach {
                    existLine = existLine.plus(1)
                    cartItems.add(OrderLine(it, 1.0, cartOrderEntityReference, existLine))

                    it.auxiliaryProductsAdd.forEach {
                        existLine = existLine.plus(1)
                        cartItems.add(OrderLine(it, 1.0, cartOrderEntityReference, existLine))
                    }
                }

                product.customProductsSelect.values.forEach {
                    existLine = existLine.plus(1)
                    cartItems.add(OrderLine(it, 1.0, cartOrderEntityReference, existLine))
                }
            }
        }

        return cartItems
    }


    fun addOrderLineToCartOrder(order: CartOrder, product: Product, quantity: Double, discount: Double = 0.0, handler: (ArrayList<OrderLine>?, Errors?) -> Unit) {

        val orderLines = createOrderLines(order, product, quantity, discount)
        val actionRequests = ArrayList<ActionRequest>()

        orderLines.forEach {
            val entity = EntityCollection.Entity(it.attribute, logicalName = "idcrm_posorderline")
            actionRequests.add(ActionRequest(ActionRequest.Action.create, entity))
        }

        DynamicsConnector.default(context).executeMultiple(actionRequests, { responseItems, errors ->

            if (errors != null) {
                handler(null, errors)
                return@executeMultiple
            }

            for (i in 0..responseItems!!.size) {
                orderLines[i].id = responseItems[i].id
            }

            handler(orderLines, null)
        })
    }

    fun getAnnotation(entityReference: EntityReference?, scaleType: ImageScaleType, handler: (Annotation?, Errors?) -> Unit) {

        appData.getAnnotation(entityReference, scaleType, { annotation, errors ->

            if (errors != null) {
                handler(null, errors)
                return@getAnnotation
            }

            handler(annotation, null)
        })
    }

    fun register(param: HashMap<String, String>?, handler: (Boolean, String?) -> Unit) {
        val request = AppRequestData(WebConfig.shared().REGISTER_URL, param)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                handler(false, result.message)
                return@AppRequestTask
            }

            handler(true, null)
        }).execute()
    }

    fun login(param: HashMap<String, String>?): Pair<AppToken?, String?> {
        val request = AppRequestData(WebConfig.shared().LOGIN_URL, param)

        val result = AppRequestTask(request).execute().get()

        if (result.isError()) {
            return null to result.message
        }

        val jsonObject = JSONObject(result.data)
        val contactId = jsonObject.getSafeString("contactId")
        SharedPreferenceUtils.shared(context).setUserId(contactId)

        return AppToken(result.data) to null
    }
}