package abm.co.feature.login

import abm.co.designsystem.R
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
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

    override fun event(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.OnEnterEmailValue -> {
                mutableState.update {
                    it.copy(email = event.value)
                }
            }
            is LoginContract.Event.OnEnterPasswordValue -> {
                mutableState.update {
                    it.copy(password = event.value)
                }
            }
            LoginContract.Event.OnLoginViaGoogleClicked -> {
                loginViaGoogle()
            }
            LoginContract.Event.OnLoginViaEmailClicked -> {
                loginViaEmail()
            }
            LoginContract.Event.OnLoginViaFacebookClicked -> {
                // TODO not realized
            }
            LoginContract.Event.OnSignUpClicked -> {
                navigateToSignUp()
            }
        }
    }


    private fun navigateToSignUp() {
        viewModelScope.launch {
            _channel.send(LoginContract.Channel.NavigateToSignUp)
        }
    }

    private fun navigateToHomePage() = viewModelScope.launch {
        _channel.send(LoginContract.Channel.NavigateToHome)
    }

    fun navigateToForgotPassword() = viewModelScope.launch(Dispatchers.IO) {
        _channel.send(LoginContract.Channel.NavigateToForgotPassword)
    }

    private fun loginViaGoogle() = viewModelScope.launch(Dispatchers.IO) {
        val intent = Intent(googleSignInClient.signInIntent)
        mutableState.update { it.copy(isGoogleButtonLoading = true) }
        _channel.send(LoginContract.Channel.LoginViaGoogle(intent))
    }

    private fun loginViaEmail() {
        viewModelScope.launch(Dispatchers.Default) {
            with(state.value) {
                when {
                    TextUtils.isEmpty(email.trim()) -> {
                        Failure.FailureSnackbar(ExpectedMessage.Res(R.string.email_empty))
                            .sendException()
                    }
                    TextUtils.isEmpty(password) -> {
                        Failure.FailureSnackbar(ExpectedMessage.Res(R.string.password_empty))
                            .sendException()
                    }
                    else -> {
                        mutableState.update { it.copy(isLoginButtonLoading = true) }
                        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                checkUserExistence()
                                if (!task.isSuccessful) {
                                    task.exception?.mapToFailure()?.sendException()
                                }
                            }
                    }
                }
            }
        }
    }

    private fun checkUserExistence(currentUser: FirebaseUser? = firebaseAuth.currentUser) {
        mutableState.update {
            it.copy(
                isLoginButtonLoading = false,
                isGoogleButtonLoading = false,
                isFacebookButtonLoading = false
            )
        }
        if (currentUser != null) {
            navigateToHomePage()
        }
    }

    fun firebaseAuthWithGoogle(intent: Intent) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let {
                val credential = GoogleAuthProvider.getCredential(it, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        checkUserExistence()
                        if (!task.isSuccessful) {
                            task.exception?.mapToFailure()?.sendException()
                        }
                    }
            }
        } catch (e: ApiException) {
            e.mapToFailure().sendException()
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(LoginContract.Channel.ShowMessage(it))
            }
        }
    }
}
