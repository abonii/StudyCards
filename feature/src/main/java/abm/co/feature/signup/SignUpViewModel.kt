package abm.co.feature.signup

import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.feature.R
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
class SignUpViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(), SignUpContract {

    private val mutableState = MutableStateFlow(SignUpContract.State())
    override val state: StateFlow<SignUpContract.State> = mutableState.asStateFlow()

    private val _channel = Channel<SignUpContract.Channel>()
    override val channel = _channel.receiveAsFlow()

    override fun event(event: SignUpContract.Event) {
        when (event) {
            is SignUpContract.Event.OnEnterEmailValue -> {
                mutableState.update {
                    it.copy(email = event.value)
                }
            }
            is SignUpContract.Event.OnEnterPasswordValue -> {
                mutableState.update {
                    it.copy(password = event.value)
                }
            }
            is SignUpContract.Event.OnEnterPasswordConfirmValue -> {
                mutableState.update {
                    it.copy(passwordConfirm = event.value)
                }
            }
            SignUpContract.Event.OnLoginViaGoogleClicked -> {
                loginViaGoogle()
            }
            SignUpContract.Event.OnLoginViaFacebookClicked -> {
                // TODO not realized
            }
            SignUpContract.Event.OnLoginClicked -> {
                navigateToLoginPage()
            }
            SignUpContract.Event.OnSignUpViaEmailClicked -> {
                signUpViaEmail()
            }
        }
    }


    private fun navigateToLoginPage() {
        viewModelScope.launch {
            _channel.send(SignUpContract.Channel.NavigateToLogin)
        }
    }

    private fun navigateToHomePage() {
        viewModelScope.launch {
            _channel.send(SignUpContract.Channel.NavigateToHome)
        }
    }

    private fun loginViaGoogle() {
        viewModelScope.launch(Dispatchers.IO) {
            val intent = Intent(googleSignInClient.signInIntent)
            mutableState.update { it.copy(isGoogleButtonLoading = true) }
            _channel.send(SignUpContract.Channel.LoginViaGoogle(intent))
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

    private fun signUpViaEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            with(state.value) {
                when {
                    TextUtils.isEmpty(email) -> {
                        mutableState.update { it.copy(errorRes = R.string.SignUpPage_EmailEmpty) }
                    }
                    TextUtils.isEmpty(password) -> {
                        mutableState.update { it.copy(errorRes = R.string.SignUpPage_PasswordEmpty) }
                    }
                    passwordConfirm != password -> {
                        mutableState.update { it.copy(errorRes = R.string.SignUpPage_PasswordsNotSame) }
                    }
                    password.length < 5 -> {
                        mutableState.update { it.copy(errorRes = R.string.SignUpPage_PasswordLengthNotCorrect) }
                    }
                    else -> {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                mutableState.update { it.copy(isGoogleButtonLoading = false) }
                                if (task.isSuccessful) {
                                    sendVerificationEmail()
                                } else {
                                    task.exception?.mapToFailure()?.sendException()
                                }
                            }
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
                        // TODO show alert that we send verification code to email
                        navigateToHomePage()
                    } else {
                        task.exception?.mapToFailure()?.sendException()
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

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(SignUpContract.Channel.ShowMessage(it))
            }
        }
    }
}
