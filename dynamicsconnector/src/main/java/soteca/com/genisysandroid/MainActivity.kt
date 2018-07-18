package soteca.com.genisysandroid

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.FetchExpression
import java.io.ByteArrayOutputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    val TAG = "tMain"
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        dialog = ProgressDialog(this)
//        dialog!!.setMessage("progressing")
//        dialog!!.setCanceledOnTouchOutside(false)
//        dialog!!.show()

//        val format = Format(0)
//        val outputStream = ByteArrayOutputStream()
//        val serializer = Persister(format)
////        val header = HeaderExecute("dadaddd.com", Triple("dads", "dad", "dad"))
//        serializer.write(WhoAmIRequestEncoder(), outputStream)
//        val content = outputStream.toString("UTF-8")

//        val connector = DynamicsConnector.default(this)
//        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
//                "https://rcdevs2s.crm4.dynamics.com/XRMServices/2011/Organization.svc",
//                "jean.stanghellini@rcset2sell.onmicrosoft.com",
//                "Nightfa8")
//        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
//                "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc",
//                "hariservice.hari@haricrm.com",
//                "M4f0${'$'}R'>ZfYSJFd'")
//        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
//                "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc",
//                "hariservice.larotisserie@haricrm.com",
//                "avm-!dT]PD?7{AZg")
//        connector.authenticate(con) { u, e ->
//
//        }

//        expression = FetchExpression.fetch(nil, entityType: "idcrm_posproduct", atPage: nil, pagingCookie: nil, select: nil, using: .singleCondition(.init(attribute: "idcrm_posproductid", operator: .equal, value: "test")), orderBy: nil, isDescending: true)

        val values = arrayListOf(FetchExpression.Values("id1"), FetchExpression.Values("id2"), FetchExpression.Values("id3", "uiName Three"), FetchExpression.Values("id4", "uiName 4", "uiType 4"))
//        val expression = FetchExpression.fetct(null, "idcrm_posproduct", null, null, null, FetchExpression.Filter.singleCondition(FetchExpression.Condition("idcrm_posproductid", FetchExpression.Operator.`in`, values = values)), null)

//        val expression = FetchExpression(FetchExpression.Entity("account"))
        val expression = FetchExpression(FetchExpression.Entity("account", attributes = arrayListOf(FetchExpression.Attributee("aaa"))))

        val outputStream: OutputStream = ByteArrayOutputStream()
        val serializer = Persister(AnnotationStrategy(), Format(0))
        serializer.write(expression, outputStream)

        val content = outputStream.toString()
        Log.d(TAG, "content: " + content)
    }


}
