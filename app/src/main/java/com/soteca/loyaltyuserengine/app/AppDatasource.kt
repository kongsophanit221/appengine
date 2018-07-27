package com.soteca.loyaltyuserengine.app

import android.content.Context
import com.soteca.loyaltyuserengine.api.WebConfig
import com.soteca.loyaltyuserengine.model.*
import com.soteca.loyaltyuserengine.model.Annotation
import com.soteca.loyaltyuserengine.util.ImageScaleType
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.model.encoder.body.ActionRequest
import soteca.com.genisysandroid.framwork.networking.Errors


interface AppDatasource {

    fun <T : BaseItem> get(type: T, reference: EntityReference, handler: (T?, Errors?) -> Unit)

    fun <T : BaseItem> getMultiple(type: T, fetchExpression: FetchExpression, handler: (ArrayList<T>?, Errors?) -> Unit)

    fun <T : BaseItem> getMultiple(type: T, alias: String, fetchExpression: FetchExpression, handler: (ArrayList<T>?, ArrayList<Annotation>?, Errors?) -> Unit)

    fun getAnnotation(entityReference: EntityReference?, scaleType: ImageScaleType, handler: (Annotation?, Errors?) -> Unit)

    fun getMultipleAnnotation(logicalName: String?, ids: ArrayList<String>?, scaleType: ImageScaleType, handler: (ArrayList<Annotation>?, Errors?) -> Unit)

    fun create(entity: EntityCollection.Entity, handler: (String?, Errors?) -> Unit)

    fun delete(logicalName: String, id: String, handler: (Boolean?, Errors?) -> Unit)

    fun update(entity: EntityCollection.Entity, handler: (Boolean?, Errors?) -> Unit)
}

open class BaseItem() {

    constructor(attribute: EntityCollection.Attribute) : this()

    open val attribute: EntityCollection.Attribute? = null
        get

    open fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return BaseItem(attribute)
    }
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

                if (annotationsAttr.size > 0) {
                    annotations.add(Annotation(EntityCollection.Attribute(annotationsAttr)))
                }
                data.add(type.initContructor(EntityCollection.Attribute(dataAttr)) as T)
            }

            handler(data, annotations, null)
        }
    }


    override fun getAnnotation(entityReference: EntityReference?, scaleType: ImageScaleType, handler: (Annotation?, Errors?) -> Unit) {

        val secondEntity = DefaultEntity(entityReference!!.logicalName!!)
        val imageCondition = FetchExpression.Condition("subject", FetchExpression.Operator.like, "%${scaleType.subjectName()}%")

        val linkEntityCondition = FetchExpression.Condition(secondEntity.primaryIdAttribute, FetchExpression.Operator.equal, entityReference.id)
        val linkEntity = FetchExpression.LinkEntity(name = entityReference.logicalName!!, from = secondEntity.primaryIdAttribute, to = "objectid", filter = FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(linkEntityCondition)))

        val expression = FetchExpression(FetchExpression.Entity(name = "annotation", linkEntities = arrayListOf(linkEntity), filter = FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(imageCondition))))

        DynamicsConnector.default(context).retrieveMultiple(expression, { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }

            entityCollection?.entityList?.first()?.let {

                handler(Annotation(it.attribute!!), null)

            } ?: run {

                handler(null, null)
            }
        })
    }

    override fun getMultipleAnnotation(logicalName: String?, ids: ArrayList<String>?, scaleType: ImageScaleType, handler: (ArrayList<Annotation>?, Errors?) -> Unit) {

        val secondEntity = DefaultEntity(logicalName!!)

        val imageCondition = FetchExpression.Condition("subject", FetchExpression.Operator.like, "%${scaleType.subjectName()}%")

        val values = ArrayList<FetchExpression.Condition.Value>()
        values.addAll(ids!!.map { FetchExpression.Condition.Value(it) })

        val linkEntityCondition = FetchExpression.Condition(attribute = secondEntity.primaryIdAttribute, `operator` = FetchExpression.Operator.`in`, values = values)
        val linkEntity = FetchExpression.LinkEntity(name = logicalName, from = secondEntity.primaryIdAttribute, to = "objectid", filter = FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(linkEntityCondition)))
        val expression = FetchExpression(FetchExpression.Entity(name = "annotation", linkEntities = arrayListOf(linkEntity), filter = FetchExpression.Filter(FetchExpression.LogicalOperator.and, arrayListOf(imageCondition))))

        DynamicsConnector.default(context).retrieveMultiple(expression, { entityCollection, errors ->

            if (errors != null) {
                handler(null, errors)
                return@retrieveMultiple
            }
            val annotations = ArrayList<Annotation>()

            entityCollection?.entityList?.map { Annotation(it.attribute!!) }?.let {
                annotations.addAll(it)
                handler(annotations, null)
            } ?: run {
                handler(null, null)
            }
        })
    }

    override fun create(entity: EntityCollection.Entity, handler: (String?, Errors?) -> Unit) {

        DynamicsConnector.default(context).create(entity, { id, errors ->

            if (errors != null) {
                handler(null, errors)
                return@create
            }

            handler(id, null)

        })
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