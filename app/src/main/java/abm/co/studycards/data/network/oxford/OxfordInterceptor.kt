package abm.co.studycards.data.network.oxford

import abm.co.studycards.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class OxfordInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.header("app_id", Constants.oxford_app_id)
        builder.header("app_key", Constants.oxford_app_key)
        return chain.proceed(builder.build())
    }
}