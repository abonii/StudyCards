package abm.co.feature.authorization.login

import abm.co.designsystem.R
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Immutable
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
) : ViewModel() {

    private val mutableState = MutableStateFlow(LoginContractState())
    val state: StateFlow<LoginContractState> = mutableState.asStateFlow()

    private val _channel = Channel<LoginContractChannel>()
    val channel = _channel.receiveAsFlow()

    fun event(event: LoginContractEvent) {
        when (event) {
            is LoginContractEvent.OnEnterEmailValue -> {
                mutableState.update {
                    it.copy(email = event.value)
                }
            }
            is LoginContractEvent.OnEnterPasswordValue -> {
                mutableState.update {
                    it.copy(password = event.value)
                }
            }
            LoginContractEvent.OnLoginViaGoogleClicked -> {
                loginViaGoogle()
            }
            LoginContractEvent.OnLoginViaEmailClicked -> {
                loginViaEmail()
            }
            LoginContractEvent.OnLoginViaFacebookClicked -> {
                // TODO not realized
            }
            LoginContractEvent.OnSignUpClicked -> {
                navigateToSignUp()
            }
            LoginContractEvent.OnLoadingDismissWanted -> {
                onLoadingDismissWanted()
            }
        }
    }


    private fun navigateToSignUp() {
        viewModelScope.launch {
            _channel.send(LoginContractChannel.NavigateToSignUp)
        }
    }

    private fun navigateToHomePage() = viewModelScope.launch {
        _channel.send(LoginContractChannel.NavigateToHome)
    }

    fun navigateToForgotPassword() = viewModelScope.launch(Dispatchers.IO) {
        _channel.send(LoginContractChannel.NavigateToForgotPassword)
    }

    private fun loginViaGoogle() = viewModelScope.launch(Dispatchers.IO) {
        val intent = Intent(googleSignInClient.signInIntent)
        mutableState.update { it.copy(isScreenLoading = true) }
        _channel.send(LoginContractChannel.LoginViaGoogle(intent))
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
        println("checkUserExistence")
        mutableState.update {
            it.copy(
                isLoginButtonLoading = false,
                isScreenLoading = false
            )
        }
        if (currentUser != null) {
            navigateToHomePage()
        }
    }

    fun firebaseAuthWithGoogle(activityResult: ActivityResult) {
        try {
            if (activityResult.resultCode == Activity.RESULT_OK) {
                activityResult.data?.let { intent ->
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
                }
            } else checkUserExistence(null)
        } catch (e: ApiException) {
            e.mapToFailure().sendException()
        }
    }

    private fun onLoadingDismissWanted() {
        viewModelScope.launch {
            _channel.send(
                LoginContractChannel.ShowMessage(
                    MessageContent.Snackbar.MessageContentRes(
                        titleRes = R.string.Messages_UsersFault_title,
                        subtitleRes = R.string.Messages_UsersFault_subtitle,
                        type = MessageType.Info
                    )
                )
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(LoginContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class LoginContractState(
    val isLoginButtonLoading: Boolean = false,
    val isScreenLoading: Boolean = false,
    val email: String = "",
    val password: String = ""
)

@Immutable
sealed interface LoginContractEvent {
    object OnLoginViaEmailClicked : LoginContractEvent
    object OnLoginViaGoogleClicked : LoginContractEvent
    object OnLoginViaFacebookClicked : LoginContractEvent
    object OnSignUpClicked : LoginContractEvent
    object OnLoadingDismissWanted : LoginContractEvent
    data class OnEnterEmailValue(val value: String) : LoginContractEvent
    data class OnEnterPasswordValue(val value: String) : LoginContractEvent
}

@Immutable
sealed interface LoginContractChannel {
    data class LoginViaGoogle(val intent: Intent) : LoginContractChannel
    object NavigateToSignUp : LoginContractChannel
    object NavigateToHome : LoginContractChannel
    object NavigateToForgotPassword : LoginContractChannel
    data class ShowMessage(val messageContent: MessageContent) : LoginContractChannel
}
