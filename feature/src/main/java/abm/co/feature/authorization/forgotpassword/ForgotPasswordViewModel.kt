package abm.co.feature.authorization.forgotpassword

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.feature.R
import android.text.TextUtils
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    private val _channel = Channel<ForgotPasswordChannel>()
    val channel = _channel.receiveAsFlow()

    fun onEvent(event: ForgotPasswordContractEvent) {
        when (event) {
            ForgotPasswordContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(ForgotPasswordChannel.OnBack)
                }
            }

            ForgotPasswordContractEvent.OnPrimaryButtonClicked -> {
                sendResetPassword(email = state.value.password.trim())
            }

            is ForgotPasswordContractEvent.OnEnterPassword -> {
                _state.update { oldState ->
                    oldState.copy(
                        password = event.value
                    )
                }
            }
        }
    }

    private fun sendResetPassword(email: String) {
        viewModelScope.launch {
            if (TextUtils.isEmpty(email)) {
                Failure.FailureSnackbar(ExpectedMessage.Res(R.string.ForgotPassword_Message_Error_emptyEmail))
                    .sendException()
                return@launch
            }
            setPrimaryButtonLoading(isLoading = true)
            firebaseAuth.sendPasswordResetEmail(email.trim())
                .addOnCompleteListener {
                    viewModelScope.launch {
                        if (it.isSuccessful) {
                            _channel.send(
                                ForgotPasswordChannel.ShowMessage(
                                    MessageContent.Snackbar.MessageContentRes(
                                        titleRes = R.string.ForgotPassword_Message_Success_title,
                                        subtitleRes = R.string.ForgotPassword_Message_Success_sendVerification,
                                        type = MessageType.Success
                                    )
                                )
                            )
                            _state.update { oldState ->
                                oldState.copy(
                                    password = ""
                                )
                            }
                        } else {
                            it.exception?.mapToFailure()?.sendException()
                        }
                        setPrimaryButtonLoading(isLoading = false)
                    }
                }
        }
    }

    private fun setPrimaryButtonLoading(isLoading: Boolean) {
        _state.update { oldState ->
            oldState.copy(
                isPrimaryButtonLoading = isLoading
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(ForgotPasswordChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class ForgotPasswordState(
    val isPrimaryButtonLoading: Boolean = false,
    val password: String = ""
)

@Immutable
sealed interface ForgotPasswordContractEvent {
    object OnPrimaryButtonClicked : ForgotPasswordContractEvent
    object OnBack : ForgotPasswordContractEvent
    data class OnEnterPassword(val value: String) : ForgotPasswordContractEvent
}

@Immutable
sealed interface ForgotPasswordChannel {
    object OnBack : ForgotPasswordChannel
    data class ShowMessage(val messageContent: MessageContent) : ForgotPasswordChannel
}
