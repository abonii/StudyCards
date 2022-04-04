package abm.co.studycards.data.network.oxford

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class OxfordInterceptor() : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}