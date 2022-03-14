package abm.co.studycards.ui.login

import abm.co.studycards.R
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun loginAnonymously() = viewModelScope.launch {
        _loading.value = true
        firebaseAuthInstance.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    checkUserExistence()
                } else {
                    makeToast("${it.exception?.message}")
                }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
                makeToast("${it.message}")
            }
    }

    fun navigateToForgotPassword() = viewModelScope.launch {
        _sharedFlow.emit(LoginEventChannel.NavigateToForgotPassword)
    }

    fun loginViaGoogle() = viewModelScope.launch {
        val intent = Intent(googleSignInClient.signInIntent)
        _sharedFlow.emit(LoginEventChannel.LoginViaGoogle(intent))
    }

    fun loginViaEmail() = viewModelScope.launch {
        when {
            TextUtils.isEmpty(email) -> {
                _error.value = App.instance.getString(R.string.email_empty)
            }
            TextUtils.isEmpty(password) -> {
                _error.value = App.instance.getString(R.string.password_empty)
            }
            else -> {
                _loading.value = true
                firebaseAuthInstance.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        _loading.value = false
                        if (it.isSuccessful) {
                            checkIfEmailVerified()
                        } else {
                            _error.value = it.exception?.message
                        }
                    }
            }
        }
    }

    private fun checkIfEmailVerified() {
        val user = firebaseAuthInstance.currentUser
        if (user?.isEmailVerified == true) {
            checkUserExistence(user)
        } else {
            makeToast(App.instance.getString(R.string.email_not_verified))
            FirebaseAuth.getInstance().signOut()
        }
    }

    fun checkUserExistence(currentUser: FirebaseUser? = firebaseAuthInstance.currentUser) {
        _loading.value = false
        if (currentUser != null) {
            navigateToMainActivity()
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        _loading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuthInstance.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserExistence()
                } else {
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
}