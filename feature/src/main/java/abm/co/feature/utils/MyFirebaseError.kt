package abm.co.feature.utils

import abm.co.designsystem.R
import androidx.annotation.StringRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException

@StringRes
fun Exception?.firebaseError(): Int {
    return try {
        throw this ?: Exception()
    } catch (e: FirebaseAuthException) {
        FirebaseError.getMessage(e.errorCode)
    } catch (e: FirebaseTooManyRequestsException) {
        R.string.error_too_many_requests
    } catch (e: FirebaseNetworkException) {
        R.string.no_internet_connection
    } catch (e: Exception) {
        R.string.some_problems_occurred
    }
}

@StringRes
fun firebaseError(code: Int): Int {
    return FirebaseError.getMessage(code)
}