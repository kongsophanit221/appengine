package soteca.com.genisysandroid

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import soteca.com.genisysandroid.framwork.connector.DynamicsConfiguration
import soteca.com.genisysandroid.framwork.connector.DynamicsConnector
import soteca.com.genisysandroid.framwork.model.EntityCollection


class MainActivity : AppCompatActivity() {
    val TAG = "tMain"
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("progressing")
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

//        val format = Format(0)
//        val outputStream = ByteArrayOutputStream()
//        val serializer = Persister(format)
////        val header = HeaderExecute("dadaddd.com", Triple("dads", "dad", "dad"))
//        serializer.write(WhoAmIRequestEncoder(), outputStream)
//        val content = outputStream.toString("UTF-8")

        val connector = DynamicsConnector.default(this)
//        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
//                "https://rcdevs2s.crm4.dynamics.com/XRMServices/2011/Organization.svc",
//                "jean.stanghellini@rcset2sell.onmicrosoft.com",
//                "Nightfa8")
        val con = DynamicsConfiguration(DynamicsConfiguration.DynamicsConnectionType.office365,
                "https://haricrm.crm5.dynamics.com/XRMServices/2011/Organization.svc",
                "hariservice.hari@haricrm.com",
                "M4f0${'$'}R'>ZfYSJFd'")
        connector.authenticate(con) { u, e ->

        }
    }


}
