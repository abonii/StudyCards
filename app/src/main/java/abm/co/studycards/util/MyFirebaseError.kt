package abm.co.studycards.util

import abm.co.studycards.R
import abm.co.studycards.data.model.MyFirebaseError
import androidx.annotation.StringRes
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
    }
}

@StringRes
fun firebaseError(code: Int): Int {
    return MyFirebaseError.getMessage(code)
}