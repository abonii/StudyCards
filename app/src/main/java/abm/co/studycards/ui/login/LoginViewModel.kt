package abm.co.studycards.ui.login

import abm.co.studycards.R
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import abm.co.studycards.util.firebaseError
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuthInstance: FirebaseAuth
) : BaseViewModel() {

    var email: String = ""
    var password: String = ""

    val dispatcher = Dispatchers.IO

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<LoginEventChannel>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateToRegistration() = viewModelScope.launch {
        _sharedFlow.emit(LoginEventChannel.NavigateToRegistration)
    }

    private fun navigateToMainActivity() = viewModelScope.launch {
        _sharedFlow.emit(LoginEventChannel.NavigateToMainActivity)
    }

    fun setLoading(b: Boolean) {
        _loading.value = b
    }

    fun loginAnonymously() = viewModelScope.launch(dispatcher) {
        delay(200)
        _loading.value = true
        firebaseAuthInstance.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    checkUserExistence()
                } else {
                    makeToast(firebaseError(it.exception))
                }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
                makeToast(firebaseError(it))
            }
    }

    fun navigateToForgotPassword() = viewModelScope.launch(dispatcher) {
        _sharedFlow.emit(LoginEventChannel.NavigateToForgotPassword)
    }

    fun navigateToEmailFragment() = viewModelScope.launch(dispatcher) {
        _sharedFlow.emit(LoginEventChannel.NavigateToEmailFragment)
    }

    fun loginViaGoogle() = viewModelScope.launch(dispatcher) {
        val intent = Intent(googleSignInClient.signInIntent)
        _loading.value = true
        _sharedFlow.emit(LoginEventChannel.LoginViaGoogle(intent))
    }

    fun loginViaEmail() = viewModelScope.launch(dispatcher) {
        when {
            TextUtils.isEmpty(email.trim()) -> {
                _error.value = App.instance.getString(R.string.email_empty)
            }
            TextUtils.isEmpty(password) -> {
                _error.value = App.instance.getString(R.string.password_empty)
            }
            else -> {
                _loading.value = true
                firebaseAuthInstance.signInWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener {
                        _loading.value = false
                        if (it.isSuccessful) {
                            checkUserExistence()
                        } else {
                            _error.value = firebaseError(it.exception)
                        }
                    }
            }
        }
    }

    fun checkUserExistence(currentUser: FirebaseUser? = firebaseAuthInstance.currentUser) {
        _loading.value = false
        if (currentUser != null) {
            navigateToMainActivity()
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuthInstance.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserExistence()
                } else {
                    makeToast(firebaseError(task.exception))
                    checkUserExistence(null)
                }
            }
    }

}

sealed class LoginEventChannel {
    data class LoginViaGoogle(val intent: Intent) : LoginEventChannel()
    object NavigateToRegistration : LoginEventChannel()
    object NavigateToMainActivity : LoginEventChannel()
    object NavigateToForgotPassword : LoginEventChannel()
    object NavigateToEmailFragment : LoginEventChannel()
}