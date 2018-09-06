package com.soteca.loyaltyuserengine.model

import android.os.Parcel
import android.os.Parcelable
import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.AliasedType
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class Annotation : BaseItem {

    companion object {

        fun create(id: String = "", fileName: String, objectId: EntityReference, documentBody: String, mimeType: String? = null): Annotation {
            return Annotation(id, mimeType, fileName, objectId, documentBody)
        }
    }

    var id: String? = null
    var fileName: String? = null
    var objectReference: EntityReference? = null
    var documentBody: String? = null
    var mimeType: String? = null

    override val attribute: EntityCollection.Attribute?
        get() {
            val attr = EntityCollection.Attribute(arrayListOf())

            mimeType?.let {
                attr["mimetype"] = EntityCollection.ValueType.string(it)
            }

            attr["filename"] = EntityCollection.ValueType.string(fileName!!)
            attr["objectid"] = EntityCollection.ValueType.entityReference(objectReference!!)
            attr["documentbody"] = EntityCollection.ValueType.string(documentBody!!)

            return attr
        }

    constructor() : super()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {

        if (attribute["annotationid"]!!.associatedValue is AliasedType) {
            id = (attribute["annotationid"]!!.associatedValue as AliasedType).value.associatedValue as? String
            fileName = (attribute["filename"]?.associatedValue as AliasedType).value.associatedValue as? String
            objectReference = (attribute["objectid"]?.associatedValue as AliasedType).value.associatedValue as? EntityReference
            documentBody = (attribute["documentbody"]?.associatedValue as AliasedType).value.associatedValue as? String
            mimeType = (attribute["mimetype"]?.associatedValue  as? AliasedType)?.value?.associatedValue as? String
        } else {
            id = attribute["annotationid"]?.associatedValue as? String
            fileName = attribute["filename"]?.associatedValue as? String
            objectReference = attribute["objectid"]?.associatedValue as? EntityReference
            documentBody = attribute["documentbody"]?.associatedValue as? String
            mimeType = attribute["mimetype"]?.associatedValue  as? String
        }

    }

    private constructor(id: String, mimeType: String?, fileName: String, objectType: EntityReference, documentBody: String) : this() {
        this.id = id
        this.mimeType = mimeType
        this.fileName = fileName
        this.objectReference = objectType
        this.documentBody = documentBody
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Annotation(attribute)
    }

    override fun toString(): String {
        return "annotationId: $id, fileName: $fileName, objRef: $objectReference, documentBody: $documentBody, mimeType: $mimeType"
    }
}