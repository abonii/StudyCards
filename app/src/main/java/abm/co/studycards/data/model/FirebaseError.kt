package abm.co.studycards.data.model

import abm.co.studycards.R
import abm.co.studycards.util.core.App


object MyFirebaseError {
    fun getMessage(code: String): String {
        return when (code) {
            "ERROR_WRONG_PASSWORD" -> {
                App.instance.getString(R.string.error_wrong_password)
            }
            "ERROR_INVALID_EMAIL" -> {
                App.instance.getString(R.string.error_invalid_email)
            }
            "ERROR_USER_NOT_FOUND" -> {
                App.instance.getString(R.string.error_user_not_found)
            }
            "ERROR_USER_DISABLED" -> {
                App.instance.getString(R.string.error_user_disabled)
            }
            "ERROR_INVALID_CREDENTIAL" -> {
                App.instance.getString(R.string.erro_invalid_credential)
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                App.instance.getString(R.string.error_email_already_in_use)
            }
            "ERROR_TOO_MANY_REQUESTS" -> {
                App.instance.getString(R.string.error_too_many_requests)
            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                App.instance.getString(R.string.error_account_exists_with_diff_credential)
            }
            "ERROR_PROVIDER_ALREADY_LINKED" -> {
                App.instance.getString(R.string.error_account_already_linked)
            }
            "ERROR_NETWORK_REQUEST_FAILED" -> {
                App.instance.getString(R.string.error_network_failed)
            }
            "ERROR_WEAK_PASSWORD" -> {
                App.instance.getString(R.string.error_weak_password)
            }
            else -> {
                App.instance.getString(R.string.something_went_wrong)
            }
        }
    }
}
