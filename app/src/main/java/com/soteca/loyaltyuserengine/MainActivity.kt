package com.soteca.loyaltyuserengine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soteca.loyaltyuserengine.model.*
import com.soteca.loyaltyuserengine.util.Scale
import com.soteca.loyaltyuserengine.util.screenScale
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.FetchExpression
import soteca.com.genisysandroid.framwork.networking.Errors
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.logging.Filter
import kotlin.reflect.KClass

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

//            Datasource.newInstance(this@MainActivity).getCategaries({ categories, error ->
//                    categories!!.forEach {
//                        Log.d("tMain", it.toString())
//                    }
//            })
            val linkEntityMax = FetchExpression.LinkEntity(name = "idcrm_order", from = "idcrm_posorderid", to = "idcrm_posorderid", alias = "maxDate", attributes = arrayListOf(FetchExpression.Attributee(""))))

            val linkEntity = FetchExpression.LinkEntity(name = "idcrm_posorderline", from = "idcrm_order", to = "idcrm_posorderid", alias = "orderItem", attributes = null)

            val entity = FetchExpression.Entity(name = "idcrm_posorder", linkEntities = FetchExpression.LinkEntity.multipleJoin(arrayListOf(linkEntityMax, linkEntity)),
                    filter = FetchExpression.Filter.andConditions(arrayListOf(FetchExpression.Condition(attribute = "statecode", operator = FetchExpression.Operator.equal, value = "0"),
                            FetchExpression.Condition(attribute = "statuscode", operator = FetchExpression.Operator.equal, value = "527210001"))))
            val expression = FetchExpression(entity)

            val outputStream: OutputStream = ByteArrayOutputStream()
            val serializer = Persister(AnnotationStrategy(), Format(0))
            serializer.write(expression, outputStream)
            val result = outputStream.toString()

            Datasource.newInstance(this@MainActivity).getMultiple(Order(), expression, { categories, error ->

            })

        }
    }
}
