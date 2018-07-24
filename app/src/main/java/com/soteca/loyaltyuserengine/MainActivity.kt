package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.api.WebConfig
import com.soteca.loyaltyuserengine.model.AppAuthenticator
import com.soteca.loyaltyuserengine.model.Datasource
import com.soteca.loyaltyuserengine.model.HistoryOrder
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector


class MainActivity : AppCompatActivity() {

    private val TAG = "tMain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val connector = DynamicsConnector.default(this)
        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
                WebConfig.shared().CRM_URL,
                WebConfig.shared().USER_NAME,
                WebConfig.shared().PASSWORD)
//        connector.authenticate(con) { u, e ->
//
//            Datasource.shared(this@MainActivity).getExistedOrders("b2d489ac-aa88-e811-8192-e0071b67cb31", { cartItem, errors ->
//
//            })

//            Datasource.shared(this@MainActivity).getLatestOrder { historyOrder, errors ->
//
//                val order: HistoryOrder = historyOrder!!
//
//                Datasource.shared(this@MainActivity).cancelOrder(order.id, { status, errors ->
//
//                    if (status!!) {
//                        Log.d("tMain", "successFul")
//                    }
//                })
//
//            }
//        }


        val authenticate = AppAuthenticator(this, "measna@haricrm.com", "123456")
        val connector = DynamicsConnector(this, authenticate)
        connector.authenticate(con, { users, error ->

        })

    }
}
