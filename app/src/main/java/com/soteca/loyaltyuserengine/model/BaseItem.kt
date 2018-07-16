package com.soteca.loyaltyuserengine.model

import android.content.Context
import android.util.Log
import com.soteca.loyaltyuserengine.util.ImageScaleType
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors

open class BaseItem() {

    constructor(attribute: EntityCollection.Attribute) : this()

    open val attributePush: EntityCollection.Attribute? = null
        get

    open fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return BaseItem(attribute)
    }
}

interface AppDatasource {

    fun <T : BaseItem> get(type: T, fetchExpression: FetchExpression, handler: (T?, Errors?) -> Unit)

    fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit)
}

// temporary, datasource from online
class Datasource(val context: Context) : AppDatasource {

    companion object {
        private var _shared: Datasource? = null
        fun newInstance(context: Context): Datasource {
            if (_shared == null) {
                _shared = Datasource(context)
            }
            return _shared!!
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

    override fun <T : BaseItem> get(type: T, fetchExpression: FetchExpression, handler: (T?, Errors?) -> Unit) {
        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->
            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }

            var data: BaseItem? = null

            entityCollection!!.entityList!!.forEach {
                //                data.add(type.initContructor(it.attribute!!) as T)
                data = type.initContructor(it.attribute!!)
            }

            handler(data as T, null)
        }
    }

    fun getCategaries(handler: (ArrayList<Category>?, Errors?) -> Unit) {

        getMultiple(Category(), FetchExpression(FetchExpression.Entity("idcrm_poscategory"))) { categories: ArrayList<Category>?, errors: Errors? ->

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

        getMultiple(Component(), FetchExpression(FetchExpression.Entity("idcrm_poscomponent")), { components: ArrayList<Component>?, errors: Errors? ->

            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }

            getMultiple(Product(), FetchExpression(FetchExpression.Entity("idcrm_posproduct")), { products, errors ->

                if (errors != null) {
                    handler(null, errors)
                    return@getMultiple
                }

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

    fun getAnnotation(entityReference: EntityReference?, scaleType: ImageScaleType, handler: (Annotation?, Errors?) -> Unit) {

        val secondEntity = DefaultEntity.from(entityReference!!.logicalName!!)

        val imageCondition = FetchExpression.Condition("subject", FetchExpression.Operator.like, "%${scaleType.subjectName()}%")

        val linkEntityCondition = FetchExpression.Condition(secondEntity.primaryIdAttribute, FetchExpression.Operator.equal, entityReference.id)
        val linkEntity = FetchExpression.LinkEntity(entityReference.logicalName, secondEntity.primaryIdAttribute, "objectid", null, null, null, null, null, null, FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(linkEntityCondition)))

        val expression = FetchExpression(FetchExpression.Entity("annotation", null, arrayListOf(linkEntity), FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(imageCondition))))

        get(Annotation(), expression) { annotation, errors ->
            if (errors != null) {
                handler(null, errors)
                return@get
            }
            val TAG = "tBaseItem"
            Log.d(TAG, "$annotation -> $errors")
            handler(annotation, errors)
        }
    }

    fun getMultipleAnnotation(logicalName: String?, ids: ArrayList<String>?, scaleType: ImageScaleType, handler: (ArrayList<Annotation>?, Errors?) -> Unit) {

        val secondEntity = DefaultEntity.from(logicalName!!)

        val imageCondition = FetchExpression.Condition("subject", FetchExpression.Operator.like, "%${scaleType.subjectName()}%")

        val values: List<FetchExpression.Values> = ids!!.map {
            FetchExpression.Values(it)
        }

        val linkEntityCondition = FetchExpression.Condition(secondEntity.primaryIdAttribute, FetchExpression.Operator.`in`, null, values)

        val linkEntity = FetchExpression.LinkEntity(logicalName, secondEntity.primaryIdAttribute, "objectid", null, null, null, null, null, null, FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(linkEntityCondition)))

        val expression = FetchExpression(FetchExpression.Entity("annotation", null, arrayListOf(linkEntity), FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(imageCondition))))

        getMultiple(Annotation(), expression) { annotations, errors ->
            if (errors != null) {
                handler(null, errors)
                return@getMultiple
            }
            val TAG = "tAnnotationMultiple"
            annotations!!.forEach {
                Log.d(TAG, "$it")
            }
            handler(annotations, errors)
        }

    }

    enum class DefaultEntity(val value: String) {
        ANNOTATION("annotation"),
        PRODUCT("idcrm_posproduct"),
        CATEGORY("idcrm_poscategory"),
        COMPONENT("idcrm_poscomponent");

        companion object {
            fun from(findValue: String): DefaultEntity = DefaultEntity.values().first { it.value == findValue }
        }

        val primaryIdAttribute: String
            get() {
                return when (this) {
                    DefaultEntity.ANNOTATION -> "annotationid"
                    DefaultEntity.PRODUCT -> "idcrm_posproductid"
                    DefaultEntity.CATEGORY -> "idcrm_poscategoryid"
                    DefaultEntity.COMPONENT -> "idcrm_poscomponentid"
                }
            }
    }

}