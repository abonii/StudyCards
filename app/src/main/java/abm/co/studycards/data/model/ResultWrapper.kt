package abm.co.studycards.data.model

import androidx.annotation.StringRes

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(
        val status: ErrorStatus? = null,
        val code: Int? = null,
        val error: String? = null,
        @StringRes val errorRes: Int? = null
    ) : ResultWrapper<Nothing>()
}

/**
 * various error status to know what happened if something goes wrong with a repository call
 */
enum class ErrorStatus {
    /**
     * error in connecting to repository (Server or Database)
     */
    NO_CONNECTION,

    /**
     * error in getting value (Json Error, Server Error, etc)
     */
    BAD_RESPONSE,

    /**
     * Time out  error
     */
    TIMEOUT,

    /**
     * an unexpected error
     */
    NOT_DEFINED,

    /**
     * bad credential
     */
    UNAUTHORIZED
}
