package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.model.Datasource
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val connector = DynamicsConnector.default(this)
        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
                "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc",
                "hariservice.larotisserie@haricrm.com",
                "avm-!dT]PD?7{AZg")
        connector.authenticate(con) { u, e ->

            //            Datasource.newInstance(this@MainActivity).getMultiple(Order(), FetchExpression(FetchExpression.Entity("idcrm_posorder")), { orders, error ->
//                orders!!.forEach {
//                    Log.d("tMain", it.toString())
//                }
//            })

            /*val TAG = "tMainActivity"
            Datasource.newInstance(this).getExistedOrders("89368b76-7585-e811-8192-e0071b67cb31") { cartOrder, errors ->
                Log.d(TAG,"${cartOrder!!.size}")
            }*/

            /*Datasource.newInstance(this).getLatestOrder { historyOrder, errors ->
                Log.d("tMainActivity", "$historyOrder -> $errors")
            }*/

            /*Datasource.newInstance(this@MainActivity).getCategaries({ categories, error ->
                    categories!!.forEach {
                        Log.d("tMain", it.toString())
                    }
            })*/

        }
    }
}
