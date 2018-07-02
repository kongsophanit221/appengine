package soteca.com.genisysandroid.framwork.networking

import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 * Created by SovannMeasna on 4/6/18.
 */
class FailResponse(val requestFail: Request, val dataFail: String) : Response {

    override val isStatusOK: Boolean
        get() = false

    override val data: String
        get() = dataFail

    override val request: Request?
        get() = requestFail

    override val error: Errors?
        get() {
            if (data == "") {
                return DynamicsError.invalidDataResponse
            } else if (data.contains("FailedAuthentication") && data.contains("Authentication Failure")) {
                return AuthenticationError.invalidCredential
            } else if (data.contains("InvalidSecurity")) {
                return AuthenticationError.invalidSecurityToken
            }

            try {
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(StringReader(data))
            } catch (e: Exception) {
                return DynamicsError.invalidDataResponse
            }
            return null
        }

}