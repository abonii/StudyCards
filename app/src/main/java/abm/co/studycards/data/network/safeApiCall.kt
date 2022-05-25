package abm.co.studycards.data.network

import abm.co.studycards.R
import abm.co.studycards.domain.model.ResultWrapper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is UnknownHostException -> {
                    ResultWrapper.Error(
                        res = R.string.no_internet_connection
                    )
                }
                is HttpException -> {
                    ResultWrapper.Error(res = R.string.problem_internet_connection)
                }
                is SocketTimeoutException -> {
                    ResultWrapper.Error(
                        res = R.string.socket_timeout_exception
                    )
                }
                else -> {
                    ResultWrapper.Error(throwable.message)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()?.let {
            Gson().fromJson(it, ErrorResponse::class.java).error
        }
    } catch (exception: Exception) {
        null
    }
}

data class ErrorResponse(
    val error: String?
)