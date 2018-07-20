package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.model.Datasource
import com.soteca.loyaltyuserengine.model.HistoryOrder
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

            Datasource.newInstance(this@MainActivity).getLatestOrder { historyOrder, errors ->

                val order: HistoryOrder = historyOrder!!

//                Datasource.newInstance(this@MainActivity).ca

            }

//            Datasource.newInstance(this@MainActivity).getOrderLine("9f2c3d85-448a-e811-81b4-e0071b659ef1", { carts, errors ->
//
//
//            })

        }
    }
}
