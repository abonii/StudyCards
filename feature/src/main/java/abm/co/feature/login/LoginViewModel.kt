package abm.co.feature.login

import abm.co.designsystem.R
import abm.co.feature.utils.firebaseError
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(), LoginContract {

    private val mutableState = MutableStateFlow(LoginContract.State())
    override val state: StateFlow<LoginContract.State> = mutableState.asStateFlow()

    private val _channel = Channel<LoginContract.Channel>()
    override val channel = _channel.receiveAsFlow()

    override fun event(event: LoginContract.Event) = when (event) {
        is LoginContract.Event.LoginViaGoogle -> {

        }
    }


    fun navigateToRegistration() = viewModelScope.launch {
        _channel.send(LoginContract.Channel.NavigateToRegistration)
    }

    private fun navigateToMainActivity() = viewModelScope.launch {
        _channel.send(LoginContract.Channel.NavigateToHome)
    }

    fun setLoading(isLoading: Boolean) {
        mutableState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun loginAnonymously() = viewModelScope.launch(Dispatchers.IO) {
        delay(200)
        setLoading(true)
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    checkUserExistence()
                } else {
//                    makeToast(firebaseError(it.exception))
                }
                setLoading(false)
            }
            .addOnFailureListener {
                setLoading(false)
//                makeToast(firebaseError(it))
            }
    }

    fun navigateToForgotPassword() = viewModelScope.launch(Dispatchers.IO) {
        _channel.send(LoginContract.Channel.NavigateToForgotPassword)
    }

    fun navigateToEmailFragment() = viewModelScope.launch(Dispatchers.IO) {
        _channel.send(LoginContract.Channel.NavigateToEmailFragment)
    }

    fun loginViaGoogle() = viewModelScope.launch(Dispatchers.IO) {
        val intent = Intent(googleSignInClient.signInIntent)
        setLoading(true)
        _channel.send(LoginContract.Channel.LoginViaGoogle(intent))
    }

    fun loginViaEmail() = viewModelScope.launch(Dispatchers.Default) {
        with(state.value) {
            when {
                TextUtils.isEmpty(email.trim()) -> {
                    mutableState.update { it.copy(errorRes = R.string.email_empty) }
                }
                TextUtils.isEmpty(password) -> {
                    mutableState.update { it.copy(errorRes = R.string.password_empty) }
                }
                else -> {
                    setLoading(true)
                    firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            setLoading(false)
                            if (task.isSuccessful) {
                                checkUserExistence()
                            } else {
                                mutableState.update { it.copy(errorRes = task.exception.firebaseError()) }
                            }
                        }
                }
            }
        }
    }

    fun checkUserExistence(currentUser: FirebaseUser? = firebaseAuth.currentUser) {
        mutableState.update {
            it.copy(isLoading = false)
        }
        if (currentUser != null) {
            navigateToMainActivity()
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserExistence()
                } else {
//                    makeToast(firebaseError(task.exception))
                    checkUserExistence(null)
                }
                setLoading(false)
            }
    }
}
