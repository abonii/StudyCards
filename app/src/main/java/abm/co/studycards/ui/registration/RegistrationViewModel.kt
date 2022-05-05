package abm.co.studycards.ui.registration

import abm.co.studycards.R
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import abm.co.studycards.util.firebaseError
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
            password.length < 5 -> {
                _error.value = App.instance.getString(R.string.password_length)
            }
            else -> {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        _loading.value = false
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                        } else {
                            _error.value = firebaseError(it.exception)
                        }
                    }
            }
        }
    }

    private fun sendVerificationEmail() {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        repository.addUserName(firebaseAuth.currentUser?.uid ?: "0_user", name)
                        makeToast(R.string.we_send_verification)
                        navigateToMainActivity()
                    } else {
                        makeToast(firebaseError(task.exception))
                    }
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
