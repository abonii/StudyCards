package abm.co.studycards.util

import abm.co.studycards.R
import abm.co.studycards.domain.model.MyFirebaseError
import androidx.annotation.StringRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException

@StringRes
fun firebaseError(exception: Exception?): Int {
    return try {
        throw exception!!
    } catch (e: FirebaseAuthException) {
        MyFirebaseError.getMessage(e.errorCode)
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
    return MyFirebaseError.getMessage(code)
}