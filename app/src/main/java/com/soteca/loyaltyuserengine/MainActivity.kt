package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.api.WebConfig
import com.soteca.loyaltyuserengine.model.Datasource
import com.soteca.loyaltyuserengine.model.HistoryOrder
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector


class MainActivity : AppCompatActivity() {

    private val TAG = "tMain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val connector = DynamicsConnector.default(this)
        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
                WebConfig.shared().CRM_URL,
                WebConfig.shared().USER_NAME,
                WebConfig.shared().PASSWORD)
        connector.authenticate(con) { u, e ->

            Datasource.shared(this@MainActivity).getExistedOrders { cartItem, errors ->

            }

            Datasource.shared(this@MainActivity).getLatestOrder { historyOrder, errors ->

                val order: HistoryOrder = historyOrder!!

                Datasource.shared(this@MainActivity).cancelOrder(order.id, { status, errors ->

                    if (status!!) {
                        Log.d("tMain", "successFul")
                    }
                })

            }
        }

//        val param: HashMap<String, String> = hashMapOf(
//                "emailaddress1" to "measna@haricrm.com",
//                "idcrm_password" to "123456",
//                "firstname" to "Ly",
//                "lastname" to "Measna",
//                "mobilephone" to "077498555",
//                "birthdate" to "08-08-1999T00:00:00Z",
//                "idcrm_companycode" to "300")

//        val param: HashMap<String, String> = hashMapOf(
//                "emailaddress1" to "timent@haricrm.com",
//                "idcrm_password" to "123456")
//
//        Datasource.shared(this).login(param, { token, error ->
//
//            Log.d("tMain", "successFul")
//
//        })
    }
}
