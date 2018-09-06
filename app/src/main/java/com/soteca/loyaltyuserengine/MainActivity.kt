package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.soteca.loyaltyuserengine.api.WebAPI
import com.soteca.loyaltyuserengine.app.Datasource
import com.soteca.loyaltyuserengine.model.CartOrder
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector


class MainActivity : AppCompatActivity() {

    private val TAG = "tMain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val connector = DynamicsConnector.default(this)
//        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
//                WebAPI.shared().CRM_URL,
//                WebAPI.shared().USER_NAME,
//                WebAPI.shared().PASSWORD)
//        connector.authenticate(con) { u, e ->

            //            Datasource.shared(this).getCategaries { arrayList, errors ->
//
//            }
//            Datasource.shared(this).getProductsComplete { products, errors ->
//
//                val cartOrder = CartOrder.shared()
//                Datasource.shared(this).addOrderLineToCartOrder(cartOrder!!, products!![3], 3.0, handler = { cartItem, error ->
//
//                })
//
////                products!!.forEach {
////
////                    val items = Datasource.shared(this).createCartItem(it.clone(), 5.0)
////                    Log.d(TAG,"size:" + items.size)
////                }
//            }

//        }


//        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
//                WebAPI.shared().CRM_URL,
//                WebAPI.shared().USER_NAME,
//                WebAPI.shared().PASSWORD)
//        val authenticate = AppAuthenticator(this, "measna@haricrm.com", "123456")
//
//        val connector = DynamicsConnector(this, authenticate)
//
//        connector.authenticate(con, { users, error ->
//            authenticate.saveToStorage()
//        })

//        val param: HashMap<String, String> = hashMapOf(
//                "emailaddress1" to "measna@haricrm.com",
//                "idcrm_password" to "123456",
//                "firstname" to "measna",
//                "lastname" to "ly",
//                "mobilephone" to "093943030",
//                "birthdate" to "1999-08-08T00:00:00Z",
//                "idcrm_companycode" to "300"
//        )
//        Datasource.shared(this).register(param, { isSuccess, message ->
//
//            if (!isSuccess) {
//                Log.d(TAG, "message: " + message)
//            }
//            Log.d(TAG, "success")
//        })
//
//        Log.d(TAG,"time: " + SystemClock.elapsedRealtime())
//        Log.d(TAG,"time: " + System.currentTimeMillis())

    }

//    override fun onResume() {
//        Log.d(TAG,"time: " + SystemClock.elapsedRealtime())
//        Log.d(TAG,"time: " + System.currentTimeMillis())
//        super.onResume()
//    }
}
