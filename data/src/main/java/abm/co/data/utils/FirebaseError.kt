package abm.co.data.utils

import abm.co.data.R
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.exception.FirebaseException
import androidx.annotation.StringRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseError

internal fun Exception?.firebaseError(): FirebaseException {
    return try {
        throw this ?: Exception()
    } catch (e: FirebaseAuthException) {
        FirebaseException(expectedMessage = ExpectedMessage.Res(getMessage(e.errorCode)))
    } catch (e: FirebaseTooManyRequestsException) {
        FirebaseException(expectedMessage = ExpectedMessage.Res(R.string.error_too_many_requests))
    } catch (e: FirebaseNetworkException) {
        FirebaseException(expectedMessage = ExpectedMessage.Res(R.string.no_internet_connection))
    } catch (e: Exception) {
        FirebaseException(expectedMessage = ExpectedMessage.String(e.message.toString()))
    }
}

@StringRes
private fun getMessage(code: String): Int {
    return when (code) {
        "ERROR_WRONG_PASSWORD" -> {
            R.string.error_wrong_password
        }
        "ERROR_INVALID_EMAIL" -> {
            R.string.error_invalid_email
        }
        "ERROR_USER_NOT_FOUND" -> {
            R.string.error_user_not_found
        }
        "ERROR_USER_DISABLED" -> {
            R.string.error_user_disabled
        }
        "ERROR_INVALID_CREDENTIAL" -> {
            R.string.erro_invalid_credential
        }
        "ERROR_EMAIL_ALREADY_IN_USE" -> {
            R.string.error_email_already_in_use
        }
        "ERROR_TOO_MANY_REQUESTS" -> {
            R.string.error_too_many_requests
        }
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
            R.string.error_account_exists_with_diff_credential
        }
        "ERROR_PROVIDER_ALREADY_LINKED" -> {
            R.string.error_account_already_linked
        }
        "ERROR_NETWORK_REQUEST_FAILED" -> {
            R.string.error_network_failed
        }
        "ERROR_WEAK_PASSWORD" -> {
            R.string.error_weak_password
        }
        else -> {
            R.string.something_went_wrong
        }
    }
}

@StringRes
private fun getMessage(code: Int): Int {
    return when (code) {
        DatabaseError.DISCONNECTED -> {
            R.string.error_disconnected_from_server
        }
        DatabaseError.EXPIRED_TOKEN -> {
            R.string.error_token_expired
        }
        DatabaseError.NETWORK_ERROR -> {
            R.string.error_network_failed
        }
        DatabaseError.PERMISSION_DENIED -> {
            R.string.error_dont_have_permission
        }
        DatabaseError.MAX_RETRIES -> {
            R.string.error_too_many_retries
        }
        DatabaseError.INVALID_TOKEN -> {
            R.string.error_token_invalid
        }
        DatabaseError.USER_CODE_EXCEPTION -> {
            R.string.error_user_code
        }
        DatabaseError.WRITE_CANCELED -> {
            R.string.error_write_canceled
        }
        DatabaseError.UNKNOWN_ERROR -> {
            R.string.error_unknown
        }
        DatabaseError.UNAVAILABLE -> {
            R.string.error_service_unavailable
        }
        else -> {
            R.string.something_went_wrong
        }
    }
}
