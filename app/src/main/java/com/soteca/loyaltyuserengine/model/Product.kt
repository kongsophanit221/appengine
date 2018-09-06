package com.soteca.loyaltyuserengine.model

import android.os.Parcel
import android.os.Parcelable
import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference

open class Product : BaseItem {

    open var id: String = ""
    open var name: String = ""
    open var price: Double = 0.0
    open var image: Annotation? = null
    open var category: EntityReference? = null
    open var venue: EntityReference? = null
    open var min: Double? = null
    open var max: Double? = null
    open var bundleId: String? = null
    open var description: String? = null
    open var isAvailable: Boolean = true

    open val auxiliaryProductsAdd: ArrayList<AuxiliaryProduct>? = null
    open var auxiliaryProducts: ArrayList<AuxiliaryProduct> = ArrayList()

    // will have product unavailable

    val entityReference: EntityReference?
        get() = EntityReference(id, "idcrm_posproduct")

    constructor()

    constructor(attribute: EntityCollection.Attribute) : super(attribute) {
        this.id = attribute["idcrm_posproductid"]?.associatedValue.toString()
        this.name = attribute["idcrm_name"]?.associatedValue.toString()
        this.price = attribute["idcrm_pricesell"]?.associatedValue as Double
        this.category = attribute["idcrm_category"]?.associatedValue as EntityReference
        this.venue = attribute["idcrm_venue"]?.associatedValue as EntityReference
        this.description = attribute["idcrm_description"]?.associatedValue?.toString() ?: ""
        this.isAvailable = attribute["statuscode"]?.associatedValue.toString() != "527210000"

        this.min = attribute["idcrm_min"]?.let {
            it.associatedValue as Double
        }
        this.max = attribute["idcrm_max"]?.let {
            it.associatedValue as Double
        }
        this.bundleId = attribute["idcrm_bundle"]?.let {
            (it.associatedValue as EntityReference).id
        }
    }

    open fun addOnComponent(product: AuxiliaryProduct) {}

    override val attribute: EntityCollection.Attribute?
        get() {
            val attr = EntityCollection.Attribute()
            attr["idcrm_posproductid"] = EntityCollection.ValueType.string(id)
            return attr
        }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        val isBundle = attribute["idcrm_isbundle"]!!.associatedValue as Boolean
        val isBelongToBundle = attribute["idcrm_belongstobundle"]!!.associatedValue as Boolean
        val isAuxiliary = attribute["idcrm_isauxiliary"]!!.associatedValue as Boolean

        if (isBundle) {
            return BundleProduct(attribute)
        } else if (!isBelongToBundle && !isAuxiliary) {
            return SingleProduct(attribute)
        } else {
            return AuxiliaryProduct(attribute)
        }
    }

    open fun clone(newProduct: Product? = null): Product {
        return newProduct!!
    }
}