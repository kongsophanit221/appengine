package soteca.com.appengine.model

import com.soteca.loyaltyuserengine.app.BaseItem
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import java.util.*

class Spending() : BaseItem() {

     var amount: Double = 0.0
     var baseAmount: Double = 0.0
     var point: Double? = null
     var pointSpend: Double? = null
     var exchangeRate: Double = 0.0
     var card: EntityReference? = null
     var venue: EntityReference? = null
     var modifiedOn: Date = Date()

     constructor(data: EntityCollection.Attribute) : this() {
        amount = data["idcrm_amount"]!!.associatedValue as Double
        baseAmount = data["idcrm_amount_base"]!!.associatedValue as Double
        point = data["idcrm_points"]?.associatedValue as? Double
        pointSpend = data["idcrm_pointspend"]?.associatedValue as? Double
        exchangeRate = data["exchangerate"]!!.associatedValue as Double
        card = data["idcrm_loyaltycard"]?.associatedValue as? EntityReference
        venue = data["idcrm_venue"]?.associatedValue as? EntityReference
        modifiedOn = data["modifiedon"]!!.associatedValue as Date
    }

    override fun initContructor(attribute: EntityCollection.Attribute): BaseItem {
        return Spending(attribute)
    }

    override fun toString(): String {
        return ""
    }

}