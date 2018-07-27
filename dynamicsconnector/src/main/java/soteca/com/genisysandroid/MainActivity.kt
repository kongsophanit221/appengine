package soteca.com.genisysandroid

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection
import soteca.com.genisysandroid.framwork.model.EntityReference
import soteca.com.genisysandroid.framwork.model.encoder.body.ActionRequest


class MainActivity : AppCompatActivity() {
    val TAG = "tMain"
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val attr = EntityCollection.Attribute(arrayListOf(
                EntityCollection.KeyValuePairOfstringanyType("idcrm_name", EntityCollection.Value(EntityCollection.ValueType.string("Order Test"))),
                EntityCollection.KeyValuePairOfstringanyType("idcrm_posorder", EntityCollection.Value(EntityCollection.ValueType.entityReference(EntityReference("eqwe-eqwqe-eqweqw-eqweq", "Logicai name"))))
        ))
        val attr1 = EntityCollection.Attribute(arrayListOf(
                EntityCollection.KeyValuePairOfstringanyType("idcrm_name", EntityCollection.Value(EntityCollection.ValueType.string("Soavnn 2")))
        ))

        val entity = EntityCollection.Entity(attr, logicalName = "idcrm_posorder", id = "bab7b75c-a190-e811-8188-e0071b67cb41")
//        val entity1 = EntityCollection.Entity(attr1, logicalName = "idcrm_posorder", id = "bbb7b75c-a190-e811-8188-e0071b67cb41")

//        val entityReference = EntityReference("bab7b75c-a190-e811-8188-e0071b67cb41", "idcrm_posorder")
//        val entityReference1 = EntityReference("bbb7b75c-a190-e811-8188-e0071b67cb41", "idcrm_posorder")

        val actionRequest = arrayListOf(
                ActionRequest(ActionRequest.Action.create, entity = entity)

        )
//dab5e504-a290-e811-8188-e0071b67cb41

        val connector = DynamicsConnector.default(this)
////        val config = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
////                "https://rcdevs2s.crm4.dynamics.com/XRMServices/2011/Organization.svc",
////                "jean.stanghellini@rcset2sell.onmicrosoft.com",
////                "Nightfa8")
////        val config = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
////                "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc",
////                "hariservice.hari@haricrm.com",
////                "M4f0${'$'}R'>ZfYSJFd'")
        val config = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
                "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc",
                "hariservice.larotisserie@haricrm.com",
                "avm-!dT]PD?7{AZg")
        connector.authenticate(config) { u, e ->

            connector.executeMultiple(actionRequest, { responseItems, errors ->

                val items = responseItems
                if (responseItems != null) {

                }
            })
        }


    }


}
