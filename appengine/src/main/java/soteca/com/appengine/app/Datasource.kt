package com.soteca.loyaltyuserengine.app

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebAPI
import com.soteca.loyaltyuserengine.model.*
import com.soteca.loyaltyuserengine.model.Annotation
import com.soteca.loyaltyuserengine.util.ImageScaleType
import soteca.com.appengine.model.Spending
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
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

        companion object {
            fun from(findValue: String): StatusReason = StatusReason.values().first { it.value == findValue }
        }
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
    private lateinit var appAuthenticator: AppAuthenticator

    private val productAttributes = arrayListOf(
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
            FetchExpression.Attributee("idcrm_isauxiliary"),
            FetchExpression.Attributee("idcrm_description"),
            FetchExpression.Attributee("statuscode")
    )

    constructor(context: Context) {

        AppAuthenticator.initFromStorage(context)?.let {
            appAuthenticator = it
        } ?: run {
            appAuthenticator = AppAuthenticator(context)
        }

        val connector = DynamicsConnector(context, appAuthenticator)
        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
                WebAPI.CRM_URL,
                WebAPI.USER_NAME,
                WebAPI.PASSWORD)
        appAuthenticator.setConfiguration(con)

        this.appData = AppDatasourceImp(connector)
        this.context = context
    }

    fun getCategaries(handler: (ArrayList<Category>?, Error?) -> Unit) {

        val attributebutes = arrayListOf(
                FetchExpression.Attributee("idcrm_poscategoryid"),
                FetchExpression.Attributee("idcrm_name")
        )

        val attributesAnnotation = arrayListOf(
                FetchExpression.Attributee("annotationid"),
                FetchExpression.Attributee("filename"),
                FetchExpression.Attributee("objectid"),
                FetchExpression.Attributee("documentbody"),
                FetchExpression.Attributee("mimetype")
        )

        val alias = "categoryImage"

        val venueCondition = FetchExpression.Condition(attribute = "idcrm_venue", operator = FetchExpression.Operator.equal, value = "E9B33228-DB04-E811-818B-E0071B659EF1")
        val imageCondition = FetchExpression.Condition(attribute = "subject", operator = FetchExpression.Operator.like, value = "%${ImageScaleType.SMALL.subjectName()}%")
        val linkEntity = FetchExpression.LinkEntity(name = "annotation", from = "objectid", to = "idcrm_poscategoryid", alias = alias, attributes = attributesAnnotation, linkType = FetchExpression.LinkType.OUTER, filter = FetchExpression.Filter.singleCondition(imageCondition))
        val entity = FetchExpression.Entity(name = "idcrm_poscategory", linkEntities = arrayListOf(linkEntity), attributes = attributebutes, filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = "0"), venueCondition)))
        val expression = FetchExpression(entity)

        appData.getMultiple(Category(), alias, expression, { categories, annotations, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            categories!!.forEach {
                it.image = annotations?.find { an -> an.objectReference!!.id == it.id }
            }

            handler(categories, null)
        })
    }

    fun getCategariesComplete(handler: (ArrayList<Category>?, Error?) -> Unit) {

        getCategaries { categories, errors ->

            if (errors != null) {
                handler(null, errors)
                return@getCategaries
            }

            getProductsComplete({ products, error ->

                if (error != null) {
                    handler(null, error)
                    return@getProductsComplete
                }

                categories!!.forEach {
                    it.products.addAll(products!!.filter { pit -> pit.category!!.id == it.id })
                }

                handler(categories, null)
            })
        }
    }

    fun getProducts(handler: (ArrayList<Product>?, Error?) -> Unit) {

        val defaultEntity = DefaultEntity("idcrm_posproduct")

        val attributesAnnotation = arrayListOf(
                FetchExpression.Attributee("annotationid"),
                FetchExpression.Attributee("filename"),
                FetchExpression.Attributee("objectid"),
                FetchExpression.Attributee("documentbody"),
                FetchExpression.Attributee("mimetype")
        )

        val alias = "productImage"

        val venueCondition = FetchExpression.Condition(attribute = "idcrm_venue", operator = FetchExpression.Operator.equal, value = "E9B33228-DB04-E811-818B-E0071B659EF1")
        val imageCondition = FetchExpression.Condition(attribute = "subject", operator = FetchExpression.Operator.like, value = "%${ImageScaleType.SMALL.subjectName()}%")
        val linkEntity = FetchExpression.LinkEntity(name = "annotation", from = "objectid", to = defaultEntity.primaryIdAttribute, linkType = FetchExpression.LinkType.OUTER, alias = alias, attributes = attributesAnnotation, filter = FetchExpression.Filter.singleCondition(imageCondition))
        val entity = FetchExpression.Entity(name = defaultEntity.logicalName, linkEntities = arrayListOf(linkEntity), attributes = productAttributes, filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value), venueCondition)))

        val expression = FetchExpression(entity = entity)

        appData.getMultiple(Product(), alias, expression, { products, annotations, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            products?.forEach {
                it.image = annotations?.find { an -> an.objectReference!!.id == it.id }
            }

            productsGlobal.addAll(products!!)

            handler(products, null)
        })
    }

    fun getHotProducts(handler: (ArrayList<Product>?, Error?) -> Unit) {

        val defaultEntity = DefaultEntity("idcrm_posproduct")
        val alias = "productImage"

        val attributesAnnotation = arrayListOf(
                FetchExpression.Attributee("annotationid"),
                FetchExpression.Attributee("filename"),
                FetchExpression.Attributee("objectid"),
                FetchExpression.Attributee("documentbody"),
                FetchExpression.Attributee("mimetype")
        )


        val venueCondition = FetchExpression.Condition(attribute = "idcrm_venue", operator = FetchExpression.Operator.equal, value = "E9B33228-DB04-E811-818B-E0071B659EF1")
        // Condition for image size
        val imageCondition = FetchExpression.Condition(attribute = "subject", operator = FetchExpression.Operator.like, value = "%${ImageScaleType.SMALL.subjectName()}%")
        val linkEntity = FetchExpression.LinkEntity(name = "annotation", from = "objectid", to = defaultEntity.primaryIdAttribute, alias = alias, attributes = attributesAnnotation, linkType = FetchExpression.LinkType.OUTER, filter = FetchExpression.Filter.singleCondition(imageCondition))
        // Hot tag Condition
        val hotTagCondition = FetchExpression.Condition(attribute = "idcrm_tag", operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = "D214EDCC-F39A-E811-81BA-E0071B659EF1", uiName = "Hot", uiType = "idcrm_recordtag"))
        val entity = FetchExpression.Entity(name = defaultEntity.logicalName, attributes = productAttributes, linkEntities = arrayListOf(linkEntity), filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = "0"), hotTagCondition, venueCondition)))
        val expression = FetchExpression(entity = entity)

        appData.getMultiple(Product(), alias, expression, { products, annotations, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            products!!.forEach {
                it.image = annotations?.find { an -> an.objectReference!!.id == it.id }
            }

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
        val venueCondition = FetchExpression.Condition(attribute = "idcrm_venue", operator = FetchExpression.Operator.equal, value = "E9B33228-DB04-E811-818B-E0071B659EF1")
        val expression = FetchExpression.fetch(entityType = "idcrm_poscomponent", attributes = attributes, filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = StateCode.ACTIVE.value), venueCondition)))

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

            getComponents { components, errorComponent ->

                if (errorComponent != null) {
                    handler(null, errorComponent)
                    return@getComponents
                }

                // Getting Auxiliary product as AddOn put in Single and Auxiliary, as Custom product put in Bundle
                components!!.forEach {
                    val auxiliary = products!!.find { pit -> pit.id == it.productId } as AuxiliaryProduct
                    val applyTo = products.find { ait -> ait.id == it.applyToId }
                    applyTo?.addOnComponent(AuxiliaryProduct(auxiliary, it.name))
                }

                // Getting Auxiliary product as Single product put in to list in Bundle product
                var bundleProducts = products!!.filter { it is BundleProduct }
                bundleProducts.forEach {
                    val bundelProduct = (it as BundleProduct)
                    bundelProduct.products.addAll(products.filter { it.bundleId == bundelProduct.id && it is AuxiliaryProduct }
                            .map { it as AuxiliaryProduct })
                }

                var finalProducts: ArrayList<Product> = arrayListOf()
                finalProducts.addAll(products.filter { it is SingleProduct || it is BundleProduct })

                handler(finalProducts, null)
            }
        }
    }

    fun getHotProductsComplete(handler: (ArrayList<Product>?, Error?) -> Unit) {

        getHotProducts { products, error ->

            if (error != null) {
                handler(null, error)
                return@getHotProducts
            }

            var list = ArrayList<Product>()
            products?.forEach {
                productsGlobal.find { git -> git.id == it.id }?.let { fit ->
                    list.add(fit)
                }

            }

            handler(list, null)

//            getComponents { components, error ->
//
//                if (error != null) {
//                    handler(null, error)
//                    return@getComponents
//                }
//
//                // Getting Auxiliary product as AddOn put in Single and Auxiliary, as Custom product put in Bundle
//                components!!.forEach {
//                    var auxiliary = products!!.find { pit -> pit.id == it.productId } as? AuxiliaryProduct
//
//                    auxiliary?.let { aux ->
//                        val applyTo = products!!.find { ait -> ait.id == it.applyToId }
//                        applyTo?.addOnComponent(AuxiliaryProduct(aux, it.name))
//                    }
//                }
//
//                // Getting Auxiliary product as Single product put in to list in Bundle product
//                var bundleProducts = products!!.filter { it is BundleProduct }
//                bundleProducts.forEach {
//                    val bundelProduct = (it as BundleProduct)
//                    bundelProduct.products.addAll(products!!.filter { it.bundleId == bundelProduct.id }.map { it as AuxiliaryProduct })
//                }
//
//                var finalProducts: ArrayList<Product> = arrayListOf()
//                finalProducts.addAll(products!!.filter { it is SingleProduct || it is BundleProduct })
//
//                handler(finalProducts, null)
//            }
        }
    }

    fun getRelatedProduct(productId: String, handler: (ArrayList<Product>?, Error?) -> Unit) {
        val venueCondition = FetchExpression.Condition(attribute = "idcrm_venue", operator = FetchExpression.Operator.equal, value = "E9B33228-DB04-E811-818B-E0071B659EF1")

        val componentCondition = FetchExpression.Condition(attribute = "idcrm_product", operator = FetchExpression.Operator.equal, value = productId)
        val linkProduct = FetchExpression.LinkEntity(name = "idcrm_posrelatedproduct", from = "idcrm_relatedproduct", to = "idcrm_posproductid", alias = "relatedProduct", filter = FetchExpression.Filter.singleCondition(componentCondition))
        val entity = FetchExpression.Entity(name = "idcrm_posproduct", linkEntities = arrayListOf(linkProduct), filter = FetchExpression.Filter.singleCondition(venueCondition))
        val expression = FetchExpression(entity = entity)

        appData.getMultiple(Product(), expression, { products, error ->

            if (error != null) {
                handler(null, Error(error.error))
                return@getMultiple
            }

            var relatedProducts = ArrayList<Product>()

            products?.forEach { pro ->
                productsGlobal.find { it.id == pro.id }?.let {
                    relatedProducts.add(it)
                }
            }
            handler(relatedProducts, null)
        })
    }

    fun getLatestOrder(handler: (HistoryOrder?, Error?) -> Unit) {

        val customerId = User.current().contactId
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
                FetchExpression.Attributee("modifiedon"),
                FetchExpression.Attributee("statuscode")
        )
        val customerCondition = FetchExpression.Condition(attribute = "idcrm_customerid", operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = customerId, uiType = "contact"))
        val entity = FetchExpression.Entity(name = "idcrm_posorder", attributes = attributesOrder, filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = "0"), FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = StatusReason.COMPLETE.value), customerCondition)), orders = arrayListOf(FetchExpression.Order(attribute = "modifiedon", descending = true)))
        val expression = FetchExpression(entity = entity, top = 1)

        appData.getMultiple(HistoryOrder(), expression) { historyOrders: ArrayList<HistoryOrder>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }
            historyOrders?.let {

                if (it.isEmpty()) {
                    handler(null, null)
                    return@getMultiple
                }

                val historyOrder = it.first()

                getOrderLines(historyOrder) { orderLines: ArrayList<OrderLine>?, _: Error? ->

                    historyOrder.addExistCartItems(orderLines!!)
                    handler(historyOrder, null)
                }
            } ?: run {
                handler(null, null)
            }
        }
    }

    fun getExistedOrders(handler: (CartOrder?, Error?) -> Unit) {

        val customerId = User.current().contactId
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
                FetchExpression.Attributee("modifiedon"),
                FetchExpression.Attributee("statuscode")
        )

        val customerCondition = FetchExpression.Condition(attribute = "idcrm_customerid", operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = customerId, uiType = "contact"))
        val orderItemLinkEntity = FetchExpression.LinkEntity(name = "idcrm_posorderline", from = "idcrm_order", to = "idcrm_posorderid", alias = alias, linkType = FetchExpression.LinkType.OUTER)
        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val statusReasonCondition = FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = StatusReason.OPEN.value)
        val filter = FetchExpression.Filter.andConditions(arrayListOf(stateCodeCondition, statusReasonCondition, customerCondition))
        val order = FetchExpression.Order(attribute = "modifiedon", descending = true)
        val entity = FetchExpression.Entity(name = "idcrm_posorder", attributes = attributesOrder, linkEntities = arrayListOf(orderItemLinkEntity), filter = filter, orders = arrayListOf(order))
        val expression = FetchExpression(entity)

        appData.connector.retrieveMultiple(expression) { entityCollection, errors ->

            if (errors != null) {
                handler(CartOrder(), Error(errors.error.localizedMessage))
                return@retrieveMultiple
            }

            val finalAlias = alias + "."
            var cartItems = ArrayList<OrderLine>()
            var allCartItems = ArrayList<OrderLine>()
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

                // Filter orderline that get only orderline of single product and bundle product
                if (cardItemsAttr.size > 0) {
                    val orderLine = OrderLine(EntityCollection.Attribute(cardItemsAttr))
                    allCartItems.add(orderLine)

                    if (productsGlobal.find { it.id == orderLine.productReference?.id } !is AuxiliaryProduct) {
                        cartItems.add(orderLine)
                    }
                }
            }

            if (cartOrder == null) {
                handler(CartOrder(), null)
                return@retrieveMultiple
            }

            // Pair OrderLine with sub OrderLine
            cartItems.forEach { orderLine ->
                val subOrderLines = allCartItems.filter { it.lineNumber == orderLine.lineNumber && it.id != orderLine.id }
                orderLine.orderLinesChild.addAll(subOrderLines)

                // Pair AuxProduct selected for Product
                orderLine.autoPairOrderLineSelect()
            }

            cartOrder!!.addExistCartItems(cartItems)

            handler(cartOrder, null)
        }
    }

    fun getLastOrders(count: Int, page: Int, pagingCookies: String? = null, handler: (ArrayList<HistoryOrder>?, String?, Error?) -> Unit) {
        val customerId = User.current().contactId

        if (customerId == "") {
            handler(null, null, Error("Customer Id null"))
            return
        }

        val customerCondition = FetchExpression.Condition(attribute = "idcrm_customerid", operator = FetchExpression.Operator.equal, value =
        FetchExpression.Condition.Value(value = customerId, uiName = null, uiType = "contact"))
        val orderItemLinkEntity = FetchExpression.LinkEntity(name = "idcrm_posorderline", from = "idcrm_order", to = "idcrm_posorderid", alias = "orderItem", linkType = FetchExpression.LinkType.OUTER, attributes = null)

        val entity = FetchExpression.Entity(name = "idcrm_posorder", linkEntities = arrayListOf(orderItemLinkEntity), filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = "0"), FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = StatusReason.COMPLETE.value), customerCondition)), orders = arrayListOf(FetchExpression.Order(attribute = "modifiedon", descending = true)))
        val expression = FetchExpression(count = count, page = page, pagingCookie = pagingCookies, entity = entity)


        appData.connector.retrieveMultiple(expression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, null, Error(errors.error))
                return@retrieveMultiple
            }

            val finalAlias = "orderItem."
            val historyOrders = ArrayList<HistoryOrder>()
            val allOrderLine = ArrayList<OrderLine>()


            val orderHisoryGroup = entityCollection!!.entityList?.groupBy { it.id }

            orderHisoryGroup?.forEach {

                var orderHistory: HistoryOrder? = null
                var cartItems = ArrayList<OrderLine>()

                it.value.forEach { entities ->

                    if (orderHistory == null) {
                        val cardOrderAttr = ArrayList<EntityCollection.KeyValuePairOfstringanyType>()
                        cardOrderAttr.addAll(entities.attribute!!.keyValuePairList!!.filter { kv -> !kv.key!!.contains(finalAlias) })
                        orderHistory = HistoryOrder(EntityCollection.Attribute(cardOrderAttr))
                    }

                    val cardItemsList = entities.attribute!!.keyValuePairList!!.filter { it.key!!.contains(finalAlias) }
                    val cardItemsAttr = ArrayList<EntityCollection.KeyValuePairOfstringanyType>()
                    cardItemsAttr.addAll(cardItemsList)

                    cardItemsAttr.forEach {
                        it.key = it.key!!.replace(finalAlias, "")
                    }

                    if (cardItemsAttr.size > 0) {
                        // Filter orderline that get only orderline of single product and bundle product
                        val orderLine = OrderLine(EntityCollection.Attribute(cardItemsAttr))
                        if (orderLine.product !is AuxiliaryProduct) {
                            cartItems.add(orderLine)
                        } else {
                            allOrderLine.add(orderLine)
                        }
                    }
                }

                cartItems.forEach {
                    it.orderLinesChild.addAll(allOrderLine.filter { sit -> it.lineNumber == sit.lineNumber })
                }
                orderHistory!!.addExistCartItems(cartItems)
                historyOrders.add(orderHistory!!)
            }

            handler(historyOrders, entityCollection.pagingCookie, null)

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
                "idcrm_amount",
                "idcrm_order"
        )

        val orderCondition = FetchExpression.Condition(attribute = "idcrm_order", operator = FetchExpression.Operator.equal, value = order.id)
        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val expression = FetchExpression.fetch(entityType = "idcrm_posorderline", attributes = attributesCart, filter = FetchExpression.Filter.andConditions(arrayListOf(orderCondition, stateCodeCondition)))

        appData.getMultiple(OrderLine(), expression) { orderLines: ArrayList<OrderLine>?, errors: Errors? ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            var finalOrderLines = ArrayList<OrderLine>()

            // Filter orderline that get only orderline of single product and bundle product
            finalOrderLines.addAll(orderLines!!.filter { orderLine ->
                productsGlobal.find { it.id == orderLine.productReference?.id } !is AuxiliaryProduct
            })

            handler(finalOrderLines, errors)
        }
    }

    fun createOrder(order: CartOrder, handler: (CartOrder?, Error?) -> Unit) {

        val customerId = User.current().contactId
        if (customerId == "") {
            handler(null, Error("Customer Id is empty"))
            return
        }
        order.setMultiExecute(false)

        order.venueName = "La Rotisserie - Group"
        order.venue = EntityReference("E9B33228-DB04-E811-818B-E0071B659EF1", "idcrm_venue", "La Rotisserie - Group")
        val entity = EntityCollection.Entity(attribute = order.attribute, logicalName = order.entityReference.logicalName)

        appData.create(entity) { id, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@create
            }

            order.id = id!!

            handler(order, null)
        }
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

    fun deleteOrderLine(order: CartOrder, orderLine: OrderLine, handle: (Boolean?, Error?) -> Unit) {

        var actionRequests = ArrayList<ActionRequest>()
        var list = ArrayList<ActionRequest>()

        actionRequests.addAll(order.orderLines.filter { it.lineNumber == orderLine.lineNumber }.flatMap { ol ->
            list.add(ActionRequest(ActionRequest.Action.delete, entityReference = ol.entityReference))
            list.addAll(ol.orderLinesChild.flatMap { olc -> listOf(ActionRequest(ActionRequest.Action.delete, entityReference = olc.entityReference)) })
            list
        })

        val lastOrderLine = order.lastOrderLine

        if (lastOrderLine != null && orderLine.lineNumber != lastOrderLine.lineNumber) {

            // Change last order linenumber to delete order line number
            lastOrderLine.lineNumber = orderLine.lineNumber

            val orderLineUpdateActionRequest = ActionRequest(ActionRequest.Action.update, lastOrderLine.entityReference.id!!, lastOrderLine.entityReference.logicalName!!, lastOrderLine.attribute)

            var listChild = ArrayList<ActionRequest>()

            // Change all last order linenumber to delete order line number
            val childOrderLineUpdateActionRequest = lastOrderLine.orderLinesChild.flatMap { col ->
                col.lineNumber = lastOrderLine.lineNumber
                listChild.add(ActionRequest(ActionRequest.Action.update, col.entityReference.id!!, col.entityReference.logicalName!!, col.attribute))

                listChild
            }
            actionRequests.add(orderLineUpdateActionRequest)
            actionRequests.addAll(childOrderLineUpdateActionRequest)
        }

        order.removeCart(orderLine)

        order.setMultiExecute(true)
        actionRequests.add(ActionRequest(ActionRequest.Action.update, id = order.entityReference.id!!, logicalName = order.entityReference.logicalName!!, attribute = order.attribute))

        appData.connector.executeMultiple(actionRequests, { _, error ->

            if (error != null) {
                handle(null, Error(error.error.message))
                return@executeMultiple
            }

            handle(true, null)
        })
    }

    fun addOrderLineToCartOrder(order: CartOrder, product: Product, quantity: Double, discount: Double = 0.0, handler: (OrderLine?, Error?) -> Unit) {

        if (order.id == "") {

            order.addCart(OrderLine(product, quantity, order))

            createOrder(order, { newCartOrder, error ->

                if (error != null) {
                    handler(null, error)
                    return@createOrder
                }
                CartOrder.shared().replace(newCartOrder!!)

                creatOrderLine(newCartOrder, product, quantity, handler = { orderLines, errorCreateOrderLine ->
                    handler(orderLines, errorCreateOrderLine)
                })
            })
        } else {

            checkCartExpire(order.id, { isExpire, error ->

                if (!isExpire) {
                    creatOrderLine(order, product, quantity, handler = { orderLines, errorCreateOrderLine ->
                        handler(orderLines, errorCreateOrderLine)
                    })
                } else {
                    order.clear()
                    addOrderLineToCartOrder(order, product, quantity, discount, handler)
                }
            })


        }
    }

    fun getAnnotation(entityReference: EntityReference?, scaleType: ImageScaleType?, handler: (Annotation?, Errors?) -> Unit) {

        appData.getAnnotation(entityReference, scaleType, { annotation, errors ->

            if (errors != null) {
                handler(null, errors)
                return@getAnnotation
            }

            handler(annotation, null)
        })
    }

    fun login(email: String, password: String, done: (error: Error?) -> Unit) {

        val param: HashMap<String, String> = hashMapOf("emailaddress1" to email, "password" to password)

        val request = AppRequestData(WebAPI.LOGIN_URL, param)

        AppRequestTask(request, { result ->

            if (result.isError()) {
                done(Error(result.message))
                return@AppRequestTask
            }

            appAuthenticator.email = email
            appAuthenticator.password = password
            appAuthenticator.saveToStorage()

            User.init(context, result.data)

            getCard(User.current().cardId, { card, _ ->

                if (card != null) {
                    User.current().updateCard(card)
                }
                done(null)
            })

        }).execute()
    }

    fun getPromotion(loyaltyProgram: EntityReference, companyProgram: EntityReference?, handler: (ArrayList<Promotion>?, Error?) -> Unit) {
        var expression: FetchExpression? = null
        val alias = "promotionImage"

        val imageCondition = FetchExpression.Condition(attribute = "subject", operator = FetchExpression.Operator.like, value = "%${ImageScaleType.SMALL.subjectName()}%")
        val linkEntity = FetchExpression.LinkEntity(name = "annotation", from = "objectid", to = "idcrm_loyaltypromotionid", alias = "promotionImage", linkType = FetchExpression.LinkType.OUTER, filter = FetchExpression.Filter.singleCondition(imageCondition))


        companyProgram?.let {
            expression = FetchExpression(entity = FetchExpression.Entity(name = "idcrm_loyaltypromotion", linkEntities = arrayListOf(linkEntity), filter = FetchExpression.Filter.orConditions(arrayListOf(
                    FetchExpression.Condition(attribute = loyaltyProgram.logicalName, operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = loyaltyProgram.id!!, uiName = loyaltyProgram.name, uiType = loyaltyProgram.logicalName)),
                    FetchExpression.Condition(attribute = companyProgram.logicalName, operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = companyProgram.id!!, uiName = companyProgram.name, uiType = companyProgram.logicalName)))
            )))
        } ?: run {
            expression = FetchExpression(entity = FetchExpression.Entity(name = "idcrm_loyaltypromotion", linkEntities = arrayListOf(linkEntity), filter =
            FetchExpression.Filter.singleCondition(FetchExpression.Condition(attribute = loyaltyProgram.logicalName, operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = loyaltyProgram.id!!, uiName = loyaltyProgram.name, uiType = loyaltyProgram.logicalName)))))
        }

        appData.getMultiple(Promotion(), alias, expression!!, { promotions, annotations, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.message))
                return@getMultiple
            }

            promotions!!.forEach {
                it.image = annotations?.find { an -> an.objectReference!!.id == it.id }
            }

            handler(promotions, null)
        })
    }

    fun getPromotion(name: String, id: String, logicalName: String, handler: (ArrayList<Promotion>?, Error?) -> Unit) {

        val expression = FetchExpression.fetch(entityType = "idcrm_loyaltypromotion", filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition(attribute = logicalName, operator = FetchExpression.Operator.equal, value = FetchExpression.Condition.Value(value = id, uiName = name, uiType = logicalName))))

        appData.getMultiple(Promotion(), expression, { promotions, errors ->

            if (errors != null) {
                handler(null, Error(errors.error.localizedMessage))
                return@getMultiple
            }
            handler(promotions, null)
        })
    }


    // 14 September 2018 - Modified by Rasy
    fun getSpendings(cardId: String, count: Int, page: Int, pagingCookies: String? = null, handler: (ArrayList<Spending>?, String?, Error?) -> Unit) {
        val customerId = User.current().cardId

        if (customerId == "") {
            handler(null, null, Error("Customer Id null"))
            return
        }
        val cardIdCondition = FetchExpression.Condition(attribute = "idcrm_loyaltycard", operator = FetchExpression.Operator.equal, value = cardId)
        val entity = FetchExpression.Entity(name = "idcrm_spending",
                filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "idcrm_venue", operator = FetchExpression.Operator.equal, value = "E9B33228-DB04-E811-818B-E0071B659EF1"), cardIdCondition)), orders = arrayListOf(FetchExpression.Order(attribute = "modifiedon", descending = true)))
        val expression = FetchExpression(count = count, page = page, pagingCookie = pagingCookies, entity = entity)

        appData.connector.retrieveMultiple(expression) { entityCollection, errors ->
            if (errors != null) {
                handler(null, null, Error(errors.error))
                return@retrieveMultiple
            }
            entityCollection?.entityList?.let {
                val spendings = it.map { Spending(it.attribute!!) }.toMutableList()
                handler(ArrayList(spendings), entityCollection.pagingCookie, null)
            } ?: run {
                handler(null, null, null)
            }
        }
    }

    fun getContactImage(contactId: String, handler: (Annotation?, Errors?) -> Unit) {

        val entityReference = EntityReference(id = contactId, logicalName = "contact", name = null)

        getAnnotation(entityReference, null, { annotation, error ->

            handler(annotation, error)
        })
    }

    fun getCard(cardId: String, handler: (Card?, Error?) -> Unit) {

        appData.get(Card(), EntityReference(cardId, "idcrm_loyaltycard", null), { card, error ->

            if (error != null) {
                getCard(cardId, handler)
                return@get
            }

            handler(card, null)
        })

    }

    fun addContactImage(contact: Contact, base64: String, handler: (Boolean?, Error?) -> Unit) {

        getContactImage(contact.idcrm_contactid) { annotation, error ->

            if (error == null && annotation == null) {

                val note = Annotation.create(fileName = contact.firstname, objectId = EntityReference(contact.idcrm_contactid, logicalName = "contact", name = null), documentBody = base64)
                val entity = EntityCollection.Entity(id = "", logicalName = "annotation", attribute = note.attribute)

                appData.create(entity, { id, error ->

                    if (error != null && id == null) {
                        handler(false, null)
                        return@create
                    }
                    handler(true, null)
                })
                return@getContactImage
            }

            annotation?.documentBody = base64
            val entity = EntityCollection.Entity(id = annotation?.id, logicalName = "annotation", attribute = annotation?.attribute)

            appData.update(entity, { status, errorUpdate ->

                if (errorUpdate == null) {
                    handler(status, null)
                } else {
                    handler(null, Error(errorUpdate.error.localizedMessage))
                }

            })
        }
    }

    fun updateOrderLines(order: CartOrder, handle: (Boolean?, Error?) -> Unit) {

        val actionRequestTransform: List<ActionRequest> = order.orderLines.flatMap {
            listOf(ActionRequest(ActionRequest.Action.update, id = it.entityReference.id!!, logicalName = it.entityReference.logicalName!!, attribute = it.attribute))
        }
        var actionRequests = ArrayList<ActionRequest>()
        actionRequests.addAll(actionRequestTransform)
        actionRequests.add(ActionRequest(ActionRequest.Action.update, id = order.entityReference.id!!, logicalName = order.entityReference.logicalName!!, attribute = order.attribute))


        appData.connector.executeMultiple(actionRequests) { _, error ->

            if (error != null) {
                handle(null, Error(error.error.message))
                return@executeMultiple
            }

            handle(true, null)
        }
    }

    fun updateOrderLine(order: CartOrder, orderLine: OrderLine, handle: (Boolean?, Error?) -> Unit) {

        order.setMultiExecute(true)

        val actionRequests = ArrayList<ActionRequest>()

        actionRequests.add(ActionRequest(ActionRequest.Action.update, id = order.entityReference.id!!, logicalName = order.entityReference.logicalName!!, attribute = order.attribute))
        actionRequests.add(ActionRequest(ActionRequest.Action.update, id = orderLine.entityReference.id!!, logicalName = orderLine.entityReference.logicalName!!, attribute = orderLine.attribute))

        orderLine.orderLinesChild.forEach {
            actionRequests.add(ActionRequest(ActionRequest.Action.update, id = it.entityReference.id!!, logicalName = it.entityReference.logicalName!!, attribute = it.attribute))
        }


        appData.connector.executeMultiple(actionRequests) { response, error ->

            if (error != null) {
                handle(null, Error(error.error.message))
                return@executeMultiple
            }

            handle(true, null)
        }
    }

    fun updateExtraProductOrderLine(order: CartOrder, orderLine: OrderLine, handle: (Boolean?, Error?) -> Unit) {

        val actionRequests = ArrayList<ActionRequest>()
        var newOrderLines = ArrayList<OrderLine>()
        var deleteOrderLines = ArrayList<OrderLine>()

        // Search new extra product that user add
        orderLine.product?.auxiliaryProductsAdd?.forEach { product ->

            val isExtraProductAdd = orderLine.orderLinesChild.find { it.product?.id == product.id } == null

            if (isExtraProductAdd) {
                val createOrderLine = OrderLine(product, orderLine.quantity, order, orderLine.lineNumber)
                val entity = EntityCollection.Entity(createOrderLine.attribute, logicalName = "idcrm_posorderline")
                actionRequests.add(ActionRequest(ActionRequest.Action.create, entity))
                newOrderLines.add(createOrderLine)
            }
        }

        // Search child order line that user remove (extra product)
        orderLine.orderLinesChild.forEach { ol ->

            val isChildOrderLineDelete = orderLine.product?.auxiliaryProductsAdd?.find { it.id == ol.product?.id } == null

            if (isChildOrderLineDelete) {
                actionRequests.add(ActionRequest(ActionRequest.Action.delete, entityReference = ol.entityReference))
                deleteOrderLines.add(ol)
            }
        }

        orderLine.orderLinesChild.addAll(newOrderLines)
        orderLine.orderLinesChild.removeAll(deleteOrderLines)

        order.setMultiExecute(true)
        actionRequests.add(ActionRequest(ActionRequest.Action.update, id = order.entityReference.id!!, logicalName = order.entityReference.logicalName!!, attribute = order.attribute))

        appData.connector.executeMultiple(actionRequests, { responseItems, error ->

            if (error != null) {
                handle(null, Error(error.error.message))
                return@executeMultiple
            }

            for (i in 0..newOrderLines.size - 1) {
                newOrderLines[i].id = responseItems!![i].id
            }

            handle(true, null)
        })

    }

    fun signOut() {
        User.current().signOut()
        appAuthenticator.clearConfiguration()
        appAuthenticator.clearSecurityToken()
    }

    private fun creatOrderLine(order: CartOrder, product: Product, quantity: Double, discount: Double = 0.0, handler: (OrderLine?, Error?) -> Unit) {
        val orderLine = createOrderLineObject(order, product, quantity, discount)
        CartOrder.shared().addCart(orderLine)

        val allOrderLines = ArrayList<OrderLine>()
        val actionRequests = ArrayList<ActionRequest>()

        allOrderLines.add(orderLine)
        allOrderLines.addAll(orderLine.orderLinesChild)

        allOrderLines.forEach {
            val entity = EntityCollection.Entity(it.attribute, logicalName = "idcrm_posorderline")
            actionRequests.add(ActionRequest(ActionRequest.Action.create, entity))
        }

        order.setMultiExecute(true)
        actionRequests.add(ActionRequest(ActionRequest.Action.update, id = order.entityReference.id!!, logicalName = order.entityReference.logicalName!!, attribute = order.attribute))

        appData.connector.executeMultiple(actionRequests, { responseItems, errors ->

            if (errors != null) {
                handler(null, Error(errors.error))
                return@executeMultiple
            }

            for (i in 0..responseItems!!.size - 2) { // - 2 because we also update cartOder
                allOrderLines[i].id = responseItems[i].id
            }

            // Set Id to Original Order line
            allOrderLines.forEach {

                if (it.product?.id == orderLine.product?.id) {
                    orderLine.id = it.id
                    return@forEach
                }

                orderLine.orderLinesChild.forEach { cit ->
                    if (it.product?.id == cit.product?.id) {
                        cit.id = it.id
                        return@forEach
                    }
                }
            }

            handler(orderLine, null)
        })
    }

    private fun createOrderLineObject(order: CartOrder, product: Product, quantity: Double, discount: Double = 0.0): OrderLine {

        var existLine = order.orderLines.size
        var orderLine: OrderLine? = null

        existLine = existLine.plus(1)
        orderLine = OrderLine(product, quantity, order, existLine, discount = discount)

        product.auxiliaryProductsAdd?.forEach {
            orderLine.orderLinesChild.add(OrderLine(it, quantity, order, existLine))
        }

        return orderLine
    }

    private fun checkCartExpire(orderId: String, handler: (Boolean, Error?) -> Unit) {

        val defaultEntity = DefaultEntity("idcrm_posorder")

        val expression = FetchExpression.fetch(entityType = defaultEntity.logicalName, attributes = arrayListOf("statuscode"), filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition(attribute = defaultEntity.primaryIdAttribute, operator = FetchExpression.Operator.equal, value = orderId)))
        appData.connector.retrieveMultiple(expression, { entityCollection, errors ->

            if (errors != null) {
                handler(false, Error(errors.error.localizedMessage))
                return@retrieveMultiple
            }

            val statusCode = StatusReason.from(entityCollection?.entityList?.first()?.attribute!!["statuscode"]?.associatedValue.toString())
            handler(StatusReason.COMPLETE == statusCode || StatusReason.CANCEL == statusCode, null)
        })
    }
}