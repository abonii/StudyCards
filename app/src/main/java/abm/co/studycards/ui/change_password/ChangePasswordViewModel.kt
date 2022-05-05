package abm.co.studycards.ui.change_password

import abm.co.studycards.R
import abm.co.studycards.util.Constants.TAG
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import abm.co.studycards.util.firebaseError
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    var oldPassword: String = ""
    var newPassword: String = ""
    private var user = firebaseAuth.currentUser
    private var email = user?.email ?: ""

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun changePassword() = viewModelScope.launch {
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            makeToast(App.instance.getString(R.string.password_empty))
            return@launch
        } else if (newPassword.length <= 5) {
            makeToast(App.instance.getString(R.string.password_length))
            return@launch
        }
        _loading.value = true
        val credential = EmailAuthProvider.getCredential(email, oldPassword)
        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user?.updatePassword(newPassword)?.addOnCompleteListener {
                    if (task.isSuccessful) {
                        makeToast(App.instance.getString(R.string.password_successfully_changed))
                    } else {
                        makeToast(firebaseError(it.exception))
                    }
                }
            } else {
                makeToast(firebaseError(task.exception))
            }
            _loading.value = false
        }
    }
}