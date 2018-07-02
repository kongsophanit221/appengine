package soteca.com.genisysandroid.framwork.networking

import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 * Created by SovannMeasna on 4/6/18.
 */
class SuccessResponse(val requestSuccess: Request, val dataSuccess: String) : Response {

    override val isStatusOK: Boolean
        get() = true

    override val data: String
        get() = dataSuccess

    override val error: Errors?
        get() {

            if (dataSuccess == "") {
                return DynamicsError.invalidDataResponse
            } else if (dataSuccess.contains("FailedAuthentication") && dataSuccess.contains("Authentication Failure"))
                return AuthenticationError.invalidCredential

            try {
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(StringReader(dataSuccess))
            } catch (e: Exception) {
                return DynamicsError.invalidDataResponse
            }
            return null
        }

    override val request: Request?
        get() = requestSuccess
}