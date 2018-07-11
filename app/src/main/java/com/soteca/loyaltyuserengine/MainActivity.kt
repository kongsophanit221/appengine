package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.model.AuxiliaryProduct
import com.soteca.loyaltyuserengine.model.Datasource
import com.soteca.loyaltyuserengine.model.ProductGroup
import com.soteca.loyaltyuserengine.model.SingleProduct
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors

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
            Datasource(this@MainActivity).getMultiple(SingleProduct(), FetchExpression(FetchExpression.Entity("idcrm_posproduct")), { singleProducts: ArrayList<SingleProduct>?, errors: Errors? ->

            })

            /*Datasource(this).getMultiple(ProductGroup(), FetchExpression(FetchExpression.Entity("idcrm_poscategory"))) { productGroup: ArrayList<ProductGroup>?, errors: Errors? ->

            }*/

            val TAG = "tMainActivity"
            Datasource(this).getMultiple(SingleProduct(), FetchExpression(FetchExpression.Entity("idcrm_posproduct"))) { singleProducts: ArrayList<SingleProduct>?, errors: Errors? ->
                singleProducts!!.forEach {
                    DynamicsConnector.default(this).retrieveMultiple(FetchExpression(FetchExpression.Entity("annotation"))) { entityCollection, errors ->
                        val entityList = entityCollection!!.entityList!!.filter { it -> it.id == it.id }
                        entityList.forEach {
                            it.attribute!!.keyValuePairList!!.forEach {
                                Log.d(TAG, "${it.key} : ${it.value!!.associatedValue}")
                            }
                        }
                    }
                }
            }
        }
    }
}
