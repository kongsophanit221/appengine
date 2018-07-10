package com.soteca.loyaltyuserengine.model

import android.content.Context
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors

open class BaseItem() {

    constructor(data: EntityCollection.Attribute) : this()

    open val attributePush: EntityCollection.Attribute? = null
        get
}

interface AppDatasource {

//    fun <T : BaseItem> get(type: T, fetchExpression: FetchExpression, handler: (T?, Errors?) -> Void)

    fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit)
}

// temporary, datasource from online
class Datasource(val context: Context) : AppDatasource {

//    override fun <T : BaseItem> get(type: T, fetchExpression: FetchExpression, handler: (T?, Errors?) -> Void) {
//
//        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->
//
//            if (errors != null) {
//                handler(null, errors)
//                return@retrieveMultiple
//            }
//            var data: BaseItem? = null
//
//            when (type) {
//
//            }
//
//            handler(data as T, null)
//        }
//    }

    override fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit) {

        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }

            var data: ArrayList<T> = ArrayList()

            when (type) {

                is Product -> {

                    entityCollection!!.entityList!!.forEach {

                        val isBundle = it.attribute!!["idcrm_isbundle"]!!.associatedValue as Boolean
                        val isBelongToBundle = it.attribute!!["idcrm_belongstobundle"]!!.associatedValue as Boolean
                        val isAuxiliary = it.attribute!!["idcrm_isauxiliary"]!!.associatedValue as Boolean

                        if (isBundle) {
                            val product = BundleProduct(it.attribute!!)
                            data.add(product as T)
                        } else if (!isBelongToBundle && !isAuxiliary) {
                            val product = SingleProduct(it.attribute!!)
                            data.add(product as T)
                        }
                    }
                }
            }

            handler(data, null)
        }
    }


}