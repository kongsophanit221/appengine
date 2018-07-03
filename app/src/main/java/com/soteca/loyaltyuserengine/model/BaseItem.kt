package com.soteca.loyaltyuserengine.model

import android.content.Context
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors

abstract class BaseItem {

    constructor(data: ArrayList<EntityCollection.KeyValuePairOfstringanyType>)

    val keyValuePairs: ArrayList<EntityCollection.KeyValuePairOfstringanyType>? = null
        get
}

interface AppDatasource {

    fun <T> get(type: T, fetchExpression: FetchExpression, handler: (T?, Errors?) -> Void) where T : BaseItem

    fun <T> getMultiple(type: T, fetchExpression: FetchExpression,
                        handler: (ArrayList<T>?, Errors?) -> Void) where T : BaseItem
}

// temporary, datasource from online
class Datasource(val context: Context) : AppDatasource {

    override fun <T : BaseItem> get(type: T, fetchExpression: FetchExpression, handler: (T?, Errors?) -> Void) {

        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }

            var data: BaseItem? = null

            when (type) {
                is BaseItem -> {
//                    data = BaseItem(entityCollection!!.entityList!!.first().attribute.keyValuePairList!!)
                }
            }

            handler(data as T, null)
        }
    }

    override fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Void) {

        DynamicsConnector.default(context).retrieveMultiple(fetchExpression) { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }

            var data: ArrayList<BaseItem>? = null

//            entityCollection!!.entityList!!.

            handler(data as ArrayList<T>, null)
        }
    }


}