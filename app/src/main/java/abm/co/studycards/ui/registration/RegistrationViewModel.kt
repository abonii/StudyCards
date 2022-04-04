package abm.co.studycards.ui.registration

import abm.co.studycards.R
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val firebaseAuthInstance: FirebaseAuth
) : BaseViewModel() {

    var email: String = ""
    var password: String = ""
    var verifyPassword: String = ""

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<RegistrationEventChannel>()
    val sharedFlow = _sharedFlow.asSharedFlow()


    fun register() {
        _loading.value = true
        when {
            TextUtils.isEmpty(email) -> {
                _error.value = App.instance.getString(R.string.email_empty)
            }
            TextUtils.isEmpty(password) -> {
                _error.value = App.instance.getString(R.string.password_empty)
            }
            TextUtils.isEmpty(verifyPassword) -> {
                _error.value = App.instance.getString(R.string.verify_password_empty)
            }
            verifyPassword.length < 5 -> {
                _error.value = App.instance.getString(R.string.password_length)
            }
            !TextUtils.equals(verifyPassword, password) -> {
                _error.value = App.instance.getString(R.string.passwords_not_same)
            }
            else -> {
                firebaseAuthInstance.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                        } else {
                            _error.value = "${it.exception?.localizedMessage}"
                        }
                        _loading.value = false
                    }
            }
        }
    }

    private fun sendVerificationEmail() {
        firebaseAuthInstance.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuthInstance.signOut()
                    makeToast(R.string.we_send_verification)
                    navigateToLoginFragment()
                } else {
                    makeToast(R.string.we_couldnt_send_verification)
                }
            }
    }

    fun navigateToLoginFragment() = viewModelScope.launch {
        _sharedFlow.emit(RegistrationEventChannel.NavigateToLogin)
    }

}

sealed class RegistrationEventChannel {
    object NavigateToLogin : RegistrationEventChannel()
}
