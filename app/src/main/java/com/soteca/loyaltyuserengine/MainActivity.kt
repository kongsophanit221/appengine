package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.model.Datasource
import com.soteca.loyaltyuserengine.util.ImageScaleType
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.FetchExpression

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

            Datasource.newInstance(this@MainActivity).getProducts { products, error ->
                val TAG = "tMainActivity"
                products!!.forEach {
                    /*Datasource.newInstance(this).getAnnotation(it.entityReference, ImageScaleType.SMALL) { annotation, errors ->
                        Log.d(TAG, "$annotation -> $error")
                    }*/

                    /*Datasource.newInstance(this).getMultipleAnnotation(it.entityReference!!.logicalName, arrayListOf(it.id), ImageScaleType.SMALL) { annotations, errors ->
                        Log.d(TAG, "${annotations!!.size}")
                    }*/
                }
            }

//            Datasource.newInstance(this@MainActivity).getCategaries({ categories, error ->
//                categories!!.forEach {
//                    Log.d("tMain", it.toString())
//                }
//            })


            /*val TAG = "tMainActivity"
            DynamicsConnector.default(this).retrieveMultiple(FetchExpression(FetchExpression.Entity("annotation"))) { entityCollection, errors ->
                Log.d(TAG, "$entityCollection")
            }*/

        }
    }
}
