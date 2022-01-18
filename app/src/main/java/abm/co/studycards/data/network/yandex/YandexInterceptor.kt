package abm.co.studycards.data.network.yandex

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class YandexInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        return chain.proceed(request)
    }
}