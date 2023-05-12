package abm.co.feature.authorization.signup

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.repository.AuthorizationRepository
import abm.co.feature.R
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
class SignUpViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth,
    private val authorizationRepository: AuthorizationRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow(SignUpContractState())
    val state: StateFlow<SignUpContractState> = mutableState.asStateFlow()

    private val _channel = Channel<SignUpContractChannel>()
    val channel = _channel.receiveAsFlow()

    fun event(event: SignUpContractEvent) {
        when (event) {
            is SignUpContractEvent.OnEnterEmailValue -> {
                mutableState.update {
                    it.copy(email = event.value)
                }
            }
            is SignUpContractEvent.OnEnterPasswordValue -> {
                mutableState.update {
                    it.copy(password = event.value)
                }
            }
            SignUpContractEvent.OnLoginViaGoogleClicked -> {
                loginViaGoogle()
            }
            SignUpContractEvent.OnLoginViaFacebookClicked -> {
                // TODO not realized
            }
            SignUpContractEvent.OnLoginClicked -> {
                navigateToLoginPage()
            }
            SignUpContractEvent.OnSignUpViaEmailClicked -> {
                signUpViaEmail()
            }

            is SignUpContractEvent.OnEnterNameValue -> {
                mutableState.update {
                    it.copy(name = event.value)
                }
            }
        }
    }


    private fun navigateToLoginPage() {
        viewModelScope.launch {
            _channel.send(SignUpContractChannel.NavigateToLogin)
        }
    }

    private fun navigateToUserAttributes() {
        viewModelScope.launch {
            _channel.send(SignUpContractChannel.NavigateToChooseUserAttributes)
        }
    }

    private fun loginViaGoogle() {
        viewModelScope.launch(Dispatchers.IO) {
            val intent = Intent(googleSignInClient.signInIntent)
            mutableState.update { it.copy(isScreenLoading = true) }
            _channel.send(SignUpContractChannel.LoginViaGoogle(intent))
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
                                } else {
                                    val user = state.value
                                    val firebaseUser = firebaseAuth.currentUser
                                    saveUserInfo(
                                        email = firebaseUser?.email ?: user.email,
                                        name = user.name
                                    )
                                }
                            }
                    }
                }
            } else checkUserExistence(null)
        } catch (e: ApiException) {
            e.mapToFailure().sendException()
        }
    }

    private fun signUpViaEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            with(state.value) {
                when {
                    TextUtils.isEmpty(email) -> {
                        Failure.FailureSnackbar(ExpectedMessage.Res(R.string.SignUpPage_EmailEmpty))
                            .sendException()
                    }
                    TextUtils.isEmpty(password) -> {
                        Failure.FailureSnackbar(ExpectedMessage.Res(R.string.SignUpPage_PasswordEmpty))
                            .sendException()
                    }
                    password.length < 6 -> {
                        Failure.FailureSnackbar(ExpectedMessage.Res(R.string.SignUpPage_PasswordLengthNotCorrect))
                            .sendException()
                    }
                    else -> {
                        mutableState.update { it.copy(isSignUpButtonLoading = true) }
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    sendVerificationEmail()
                                    saveUserInfo(
                                        name = name.trim(),
                                        email = email.trim(),
                                        password = password
                                    )
                                } else {
                                    mutableState.update { it.copy(isSignUpButtonLoading = false) }
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
                        _channel.send(
                            SignUpContractChannel.ShowMessage(
                                MessageContent.Snackbar.MessageContentRes(
                                    titleRes = R.string.SignUpPage_WeSentVerification_title,
                                    subtitleRes = R.string.SignUpPage_WeSentVerification_subtitle,
                                    type = MessageType.Success
                                )
                            )
                        )
                        checkUserExistence()
                    } else {
                        task.exception?.mapToFailure()?.sendException()
                    }
                }
            }
    }

    private fun checkUserExistence(currentUser: FirebaseUser? = firebaseAuth.currentUser) {
        viewModelScope.launch {
            mutableState.update {
                it.copy(
                    isSignUpButtonLoading = false,
                    isScreenLoading = false
                )
            }
            if (currentUser != null) {
                navigateToUserAttributes()
            }
        }
    }

    private fun saveUserInfo(
        name: String? = null,
        email: String? = null,
        password: String? = null
    ) {
        viewModelScope.launch {
            authorizationRepository.setUserInfo(
                name = name,
                email = email,
                password = password
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(SignUpContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class SignUpContractState(
    val isSignUpButtonLoading: Boolean = false,
    val isScreenLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
)

@Immutable
sealed interface SignUpContractEvent {
    object OnSignUpViaEmailClicked : SignUpContractEvent
    object OnLoginViaGoogleClicked : SignUpContractEvent
    object OnLoginViaFacebookClicked : SignUpContractEvent
    object OnLoginClicked : SignUpContractEvent
    data class OnEnterEmailValue(val value: String) : SignUpContractEvent
    data class OnEnterNameValue(val value: String) : SignUpContractEvent
    data class OnEnterPasswordValue(val value: String) : SignUpContractEvent
}

@Immutable
sealed interface SignUpContractChannel {
    data class LoginViaGoogle(val intent: Intent) : SignUpContractChannel
    object NavigateToLogin : SignUpContractChannel
    object NavigateToChooseUserAttributes : SignUpContractChannel
    data class ShowMessage(val messageContent: MessageContent) : SignUpContractChannel
}
