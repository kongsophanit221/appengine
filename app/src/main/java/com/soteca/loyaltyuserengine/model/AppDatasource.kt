package com.soteca.loyaltyuserengine.model

import android.content.Context
import android.util.Log
import com.soteca.loyaltyuserengine.util.ImageScaleType
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors


interface AppDatasource {

    fun <T : BaseItem> get(type: T, reference: EntityReference, handler: (T?, Errors?) -> Unit)

    fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit)

    fun <T : BaseItem> getMultiple(type: T, alias: String, fetchExpression: FetchExpression, handler: (ArrayList<T>?, ArrayList<Annotation>?, Errors?) -> Unit)

    fun delete(logicalName: String, id: String, handler: (Boolean?, Errors?) -> Unit)

    fun update(entity: EntityCollection.Entity, handler: (Boolean?, Errors?) -> Unit)

}

class AppDatasourceImp(val context: Context) : AppDatasource {

    override fun <T : BaseItem> get(type: T, reference: EntityReference, handler: (T?, Errors?) -> Unit) {
        val primaryId = DefaultEntity(reference.logicalName!!).primaryIdAttribute
        val expression = FetchExpression.fetct(count = 1, entityType = reference.logicalName!!, filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition(primaryId, FetchExpression.Operator.equal, reference.id)))

        DynamicsConnector.default(context).retrieveMultiple(expression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }
            val entity = entityCollection!!.entityList!!.single()
            handler(type.initContructor(entity.attribute!!) as T, null)
        }
    }

    override fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit) {

        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }

            var data: ArrayList<T> = ArrayList()

            entityCollection!!.entityList?.forEach {
                data.add(type.initContructor(it.attribute!!) as T)
            }

            handler(data, null)
        }
    }

    override fun <T : BaseItem> getMultiple(type: T, alias: String, fetchExpression: FetchExpression, handler: (ArrayList<T>?, ArrayList<Annotation>?, Errors?) -> Unit) {

        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, null, errors)
                return@retrieveMultiple
            }

            val finalAlias = alias + "."
            var data: ArrayList<T> = ArrayList()
            var annotations: ArrayList<Annotation> = ArrayList()

            entityCollection!!.entityList!!.forEach {
                val annotationsList = it.attribute!!.keyValuePairList!!.filter { kv -> kv.key!!.contains(finalAlias) }
                var annotationsAttr = ArrayList<EntityCollection.KeyValuePairOfstringanyType>()
                annotationsAttr.addAll(annotationsList)

                val dataAttr = ArrayList<EntityCollection.KeyValuePairOfstringanyType>()
                dataAttr.addAll(it.attribute!!.keyValuePairList!!.filter { kv -> !kv.key!!.contains(finalAlias) })

                annotationsAttr.forEach { it.key = it.key!!.replace(finalAlias, "", true) }

                annotations.add(Annotation(EntityCollection.Attribute(annotationsAttr)))
                data.add(type.initContructor(EntityCollection.Attribute(dataAttr)) as T)
            }


            handler(data, annotations, null)
        }
    }

    override fun delete(logicalName: String, id: String, handler: (Boolean?, Errors?) -> Unit) {

        DynamicsConnector.default(context).delete(logicalName, id, { delete, errors ->

            if (errors != null) {
                handler(null, errors)
                return@delete
            }

            handler(delete, errors)

        })
    }

    override fun update(entity: EntityCollection.Entity, handler: (Boolean?, Errors?) -> Unit) {

        DynamicsConnector.default(context).update(entity, { isUpdated, errors ->

            if (errors != null) {
                handler(null, errors)
                return@update
            }

            handler(isUpdated, errors)

        })
    }
}

open class BaseItem() {

    constructor(attribute: EntityCollection.Attribute) : this()

    open val attributePush: EntityCollection.Attribute? = null
        get

    open fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return BaseItem(attribute)
    }
}

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
        fun newInstance(context: Context): Datasource {
            if (_shared == null) {
                _shared = Datasource(context)
            }
            return _shared!!
        }
    }

    private var appData: AppDatasourceImp

    constructor(context: Context) {
        this.appData = AppDatasourceImp(context)
    }

    fun getCategaries(handler: (ArrayList<Category>?, Errors?) -> Unit) {

        val attributes = arrayListOf(
                "idcrm_poscategoryid",
                "idcrm_name"
        )
        val expression = FetchExpression.fetct(entityType = "idcrm_poscategory", attributes = attributes, filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))

        appData.getMultiple(Category(), expression) { categories: ArrayList<Category>?, errors: Errors? ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }

            handler(categories, null)
        }

    }

    fun getCategariesComplete(handler: (ArrayList<Category>?, Errors?) -> Unit) {

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
                handler(null, errors)
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

    fun getProducts(handler: (ArrayList<Product>?, Errors?) -> Unit) {

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
        val linkEntity = FetchExpression.LinkEntity(name = "annotation", from = "objectid", to = defaultEntity.primaryIdAttribute, alias = alias, attributes = attributesAnnotation, filter = FetchExpression.Filter.singleCondition(imageCondition))
        val entity = FetchExpression.Entity(name = defaultEntity.logicalName, attributes = attributes, linkEntities = arrayListOf(linkEntity), filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = "0")))

        val expression = FetchExpression(entity = entity)

        appData.getMultiple(Product(), alias, expression, { products, annotations, errors ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }

            products!!.forEach {
                it.image = annotations?.find { an -> an.objectReference!!.id == it.id }
            }

            productsGlobal.addAll(products!!)

            handler(products, null)
        })
    }

    fun getComponents(handler: (ArrayList<Component>?, Errors?) -> Unit) {

        val attributes = arrayListOf(
                "idcrm_poscomponentid",
                "idcrm_name",
                "idcrm_product",
                "idcrm_applyto"
        )
        val expression = FetchExpression.fetct(entityType = "idcrm_poscomponent", attributes = attributes, filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))

        appData.getMultiple(Component(), expression, { components: ArrayList<Component>?, errors: Errors? ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }
            handler(components, null)
        })
    }

    fun getProductsComplete(handler: (ArrayList<Product>?, Errors?) -> Unit) {

        getProducts { products, errors ->

            getComponents { components, errors ->

                // Getting Auxiliary product as AddOn put in Single and Auxiliary, as Custom product put in Bundle
                components!!.forEach {
                    val auxiliary = products!!.filter { pit -> pit.id == it.productId }.single() as AuxiliaryProduct
                    val applyTo = products!!.filter { ait -> ait.id == it.applyToId }.single()
                    applyTo.addOnComponent(AuxiliaryProduct(auxiliary, it.name))
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

    // Wait to add condition for specific customer
    fun getLatestOrder(handler: (HistoryOrder?, Errors?) -> Unit) {

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

        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val statusReasonCondition = FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = StatusReason.COMPLETE.value)
        val conditions = arrayListOf(stateCodeCondition, statusReasonCondition)
        val filters = FetchExpression.Filter.andConditions(conditions)
        val orders = arrayListOf(FetchExpression.Order(attribute = "modifiedon", descending = true))

        val entity = FetchExpression.Entity(name = "idcrm_posorder", attributes = attributesOrder, filter = filters, orders = orders)
        val expression = FetchExpression(entity = entity, top = 1)

        appData.getMultiple(HistoryOrder(), expression) { historyOrders: ArrayList<HistoryOrder>?, errors: Errors? ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }
            val historyOrder: HistoryOrder? = historyOrders?.let { it.single() }

            historyOrder?.let {

                getOrderLine(it.id) { cartItems: ArrayList<CartItem>?, errors: Errors? ->
                    
                    it.addExistCartItems(cartItems!!)
                    handler(it, errors)
                }
            } ?: run {
                handler(null, null)
            }
        }
    }

    fun getOrderLine(id: String, handler: (ArrayList<CartItem>?, Errors?) -> Unit) {

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

        val orderCondition = FetchExpression.Condition(attribute = "idcrm_order", operator = FetchExpression.Operator.equal, value = id)
        val stateCodeCondition = FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = StateCode.ACTIVE.value)
        val expression = FetchExpression.fetct(entityType = "idcrm_posorderline", attributes = attributesCart, filter = FetchExpression.Filter.andConditions(arrayListOf(orderCondition, stateCodeCondition)))

        appData.getMultiple(CartItem(), expression) { cartItems: ArrayList<CartItem>?, errors: Errors? ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }
            handler(cartItems, errors)
        }
    }

    fun deleteCartItem(id: String, handler: (Boolean?, Errors?) -> Unit) {

        appData.delete("idcrm_posorderline", id, { status, errors ->

            if (errors != null) {
                handler(null, errors)
                return@delete
            }

            handler(status, null)
        })
    }

    fun cancelOrder(orderId: String, handler: (Boolean?, Errors?) -> Unit) {

        val keyValuePairStatusCode = EntityCollection.KeyValuePairOfstringanyType(key = "statuscode", valueType = EntityCollection.Value(EntityCollection.ValueType.optionSetValue(value = StatusReason.CANCEL.value)))
        val entity = EntityCollection.Entity(id = orderId, logicalName = "idcrm_posorder", attribute = EntityCollection.Attribute(keyValuePairList = arrayListOf(keyValuePairStatusCode)))

        appData.update(entity) { status, errors ->

            if (errors != null) {
                handler(null, errors)
                return@update
            }

            handler(status, null)
        }
    }


//    fun getAnnotation(entityReference: EntityReference?, scaleType: ImageScaleType, handler: (Annotation?, Errors?) -> Unit) {
//
//        val secondEntity = DefaultEntity.from(entityReference!!.logicalName!!)
//
//        val imageCondition = FetchExpression.Condition("subject", FetchExpression.Operator.like, "%${scaleType.subjectName()}%")
//
//        val linkEntityCondition = FetchExpression.Condition(secondEntity.primaryIdAttribute, FetchExpression.Operator.equal, entityReference.id)
//        val linkEntity = FetchExpression.LinkEntity(entityReference.logicalName, secondEntity.primaryIdAttribute, "objectid", null, null, null, null, null, null, FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(linkEntityCondition)))
//
//        val expression = FetchExpression(FetchExpression.Entity("annotation", null, arrayListOf(linkEntity), FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(imageCondition))))
//
//        get(Annotation(), expression) { annotation, errors ->
//            if (errors != null) {
//                handler(null, errors)
//                return@get
//            }
//            val TAG = "tBaseItem"
//            Log.d(TAG, "$annotation -> $errors")
//            handler(annotation, errors)
//        }
//    }

//    fun getMultipleAnnotation(logicalName: String?, ids: ArrayList<String>?, scaleType: ImageScaleType, handler: (ArrayList<Annotation>?, Errors?) -> Unit) {
//
//        val secondEntity = DefaultEntity.from(logicalName!!)
//
//        val imageCondition = FetchExpression.Condition("subject", FetchExpression.Operator.like, "%${scaleType.subjectName()}%")
//
//        val values: List<FetchExpression.Values> = ids!!.map {
//            FetchExpression.Values(it)
//        }
//
//        val linkEntityCondition = FetchExpression.Condition(secondEntity.primaryIdAttribute, FetchExpression.Operator.`in`, null, values)
//
//        val linkEntity = FetchExpression.LinkEntity(logicalName, secondEntity.primaryIdAttribute, "objectid", null, null, null, null, null, null, FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(linkEntityCondition)))
//
//        val expression = FetchExpression(FetchExpression.Entity("annotation", null, arrayListOf(linkEntity), FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(imageCondition))))
//
//        getMultiple(Annotation(), expression) { annotations, errors ->
//            if (errors != null) {
//                handler(null, errors)
//                return@getMultiple
//            }
//            val TAG = "tAnnotationMultiple"
//            annotations!!.forEach {
//                Log.d(TAG, "$it")
//            }
//            handler(annotations, errors)
//        }
//
//    }
}