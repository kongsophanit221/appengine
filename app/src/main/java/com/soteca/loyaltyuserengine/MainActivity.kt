package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.api.WebConfig
import com.soteca.loyaltyuserengine.model.Datasource
import com.soteca.loyaltyuserengine.model.Order
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

            //            Datasource.shared(this).createOrder(Order(name = "Order Testing")) { orderId, errors ->
//                Log.d(TAG, "$orderId")
//            }

            /*Datasource.shared(this).getProducts { products, errors ->
                val product = products!!.first()
                Datasource.shared(this).addProductToCart(product) { status, errors ->
                    Log.d(TAG, "$status -> $errors")
                }
            }*/
        }
    }
}
