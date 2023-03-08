package abm.co.feature.utils

import abm.co.designsystem.R
import androidx.annotation.StringRes
import com.google.firebase.database.DatabaseError

object FirebaseError {
    @StringRes
    fun getMessage(code: String): Int {
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
    fun getMessage(code: Int): Int {
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
}
