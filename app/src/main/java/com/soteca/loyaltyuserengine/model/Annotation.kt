package com.soteca.loyaltyuserengine.model

import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

class Annotation : BaseItem {

    private var attribute: EntityCollection.Attribute? = null

    constructor()

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Annotation(attribute)
    }

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.attribute = attribute
    }

    var id: String? = ""
        get() = attribute!!["annotationid"]?.associatedValue.toString()
    var fileName: String? = ""
        get() = attribute!!["filename"]?.associatedValue.toString()
    var objectReference: EntityReference? = null
        get() = attribute!!["objectid"]?.associatedValue as EntityReference
    var documentBody: String? = ""
        get() = attribute!!["documentbody"]?.associatedValue.toString()
    var mimeType: String? = ""
        get() = attribute!!["mimetype"]?.associatedValue.toString()

    override fun toString(): String {
        return "annotationId: $id, fileName: $fileName, objRef: $objectReference, documentBody: $documentBody, mimeType: $mimeType"
    }
}