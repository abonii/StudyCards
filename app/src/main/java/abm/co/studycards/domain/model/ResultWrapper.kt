package abm.co.studycards.domain.model

import abm.co.studycards.R
import androidx.annotation.StringRes

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(
        val error: String? = null,
        @StringRes val res: Int = R.string.something_went_wrong
    ) : ResultWrapper<Nothing>()
    object Loading: ResultWrapper<Nothing>()
}