package abm.co.studycards.ui.registration

import abm.co.studycards.R
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: ServerCloudRepository
) : BaseViewModel() {

    private val firebaseAuth = repository.firebaseAuth
    var email: String = ""
    var name: String = ""
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
            TextUtils.isEmpty(name) -> {
                _error.value = App.instance.getString(R.string.email_empty)
            }
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
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                            repository.addUserName(name)
                        } else {
                            _error.value = "${it.exception?.localizedMessage}"
                        }
                        _loading.value = false
                    }
            }
        }
    }

    private fun sendVerificationEmail() {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    makeToast(R.string.we_send_verification)
                    navigateToMainActivity()
                } else {
                    makeToast(R.string.we_couldnt_send_verification)
                }
            }
    }

    private fun navigateToMainActivity() = viewModelScope.launch {
        _sharedFlow.emit(RegistrationEventChannel.NavigateToMainActivity)
    }

}

sealed class RegistrationEventChannel {
    object NavigateToMainActivity : RegistrationEventChannel()
}
