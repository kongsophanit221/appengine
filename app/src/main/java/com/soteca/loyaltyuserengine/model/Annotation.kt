package com.soteca.loyaltyuserengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.AliasedType
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class Annotation : BaseItem {

    var id: String = ""
    var fileName: String = ""
    var objectReference: EntityReference? = null
    var documentBody: String = ""
    var mimeType: String? = null

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        id = (attribute!!["annotationid"]!!.associatedValue as AliasedType).value.associatedValue as String
        fileName = (attribute!!["filename"]!!.associatedValue as AliasedType).value.associatedValue as String
        objectReference = (attribute!!["objectid"]!!.associatedValue as AliasedType).value.associatedValue as EntityReference
        documentBody = (attribute!!["documentbody"]!!.associatedValue as AliasedType).value.associatedValue as String
        mimeType = (attribute!!["mimetype"]?.associatedValue  as? AliasedType)?.value?.associatedValue as String
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Annotation(attribute)
    }

    override fun toString(): String {
        return "annotationId: $id, fileName: $fileName, objRef: $objectReference, documentBody: $documentBody, mimeType: $mimeType"
    }
}