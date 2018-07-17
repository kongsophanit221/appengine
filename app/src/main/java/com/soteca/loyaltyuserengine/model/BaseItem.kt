package com.soteca.loyaltyuserengine.model

import android.content.Context
import android.util.Log
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors
import kotlin.math.exp
import kotlin.reflect.KClass

open class BaseItem() {

    constructor(attribute: EntityCollection.Attribute) : this()

    open val attributePush: EntityCollection.Attribute? = null
        get

    open fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return BaseItem(attribute)
    }
}

interface AppDatasource {

    fun <T : BaseItem> get(type: T, reference: EntityReference, handler: (T?, Errors?) -> Unit)

    fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit)
}

// temporary, datasource from online
class Datasource(val context: Context) : AppDatasource {

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

    val entityPrimaryIdMap: HashMap<String, String> = hashMapOf("annotation" to "annotationid", "idcrm_posproduct" to "idcrm_posproductid", "idcrm_poscategory" to "idcrm_poscategoryid", "idcrm_poscomponent" to "idcrm_poscomponentid")

    override fun <T : BaseItem> get(type: T, reference: EntityReference, handler: (T?, Errors?) -> Unit) {
        val primaryId = entityPrimaryIdMap[reference.logicalName]
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

            entityCollection!!.entityList!!.forEach {
                data.add(type.initContructor(it.attribute!!) as T)
            }

            handler(data, null)
        }
    }

    fun getCategaries(handler: (ArrayList<Category>?, Errors?) -> Unit) {

        val expression = FetchExpression.fetct(entityType = "idcrm_poscategory", filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))
        getMultiple(Category(), expression) { categories: ArrayList<Category>?, errors: Errors? ->

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

        val expression = FetchExpression.fetct(entityType = "idcrm_poscomponent", filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))
        getMultiple(Component(), expression, { components: ArrayList<Component>?, errors: Errors? ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }

            val expression = FetchExpression.fetct(entityType = "idcrm_posproduct", filter = FetchExpression.Filter.singleCondition(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, value = "0")))
            getMultiple(Product(), expression, { products, errors ->

                if (errors != null) {
                    handler(null, errors)
                    return@getMultiple
                }

                productsGlobal.addAll(products!!)

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
            })
        })
    }

    fun getLatestOrder(handler: (Order?, Errors?) -> Unit) {

        val conditions = arrayListOf(FetchExpression.Condition("statecode", FetchExpression.Operator.equal, "0"), FetchExpression.Condition("statuscode", FetchExpression.Operator.equal, "527210001"))

        val expression = FetchExpression.fetct(null, "idcrm_posorder", null, null, null, FetchExpression.Filter.andConditions(conditions), "modifiedon", true)

        getMultiple(HistoryOrder(), expression) { orders: ArrayList<Order>?, errors: Errors? ->
            handler(orders!!.first(), errors)
        }
    }

    fun getOrderLine(handler: (ArrayList<CartItem>?, Errors?) -> Unit) {

        val condition = FetchExpression.Condition("statecode", FetchExpression.Operator.equal, "0")

        val expression = FetchExpression.fetct(null, "idcrm_posorderline", null, null, null, FetchExpression.Filter.singleCondition(condition))

        getMultiple(CartItem(), expression) { cartItems: ArrayList<CartItem>?, errors: Errors? ->
            handler(cartItems, errors)
        }
    }

}