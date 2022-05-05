package abm.co.studycards.util

import abm.co.studycards.R
import abm.co.studycards.data.model.MyFirebaseError
import abm.co.studycards.util.core.App
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException

fun firebaseError(exception: Exception?): String {
    return try {
        throw exception!!
    } catch (e: FirebaseAuthException) {
        MyFirebaseError.getMessage(e.errorCode)
    } catch (e: FirebaseTooManyRequestsException) {
        App.instance.getString(R.string.error_too_many_requests)
    }
}