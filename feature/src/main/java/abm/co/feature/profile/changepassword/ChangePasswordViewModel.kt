package abm.co.feature.profile.changepassword

import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.repository.AuthorizationRepository
import abm.co.feature.R
import android.text.TextUtils
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authorizationRepository: AuthorizationRepository
) : ViewModel() {

    private val _channel = Channel<ChangePasswordContractChannel>()
    val channel: Flow<ChangePasswordContractChannel> = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(ChangePasswordContractState())
    val state: StateFlow<ChangePasswordContractState> = _state.asStateFlow()

    private val currentUser get() = firebaseAuth.currentUser

    fun onEvent(event: ChangePasswordContractEvent) {
        when (event) {
            ChangePasswordContractEvent.OnClickPrimaryButton -> {
                onPrimaryButtonClicked(
                    currentPassword = state.value.currentPassword,
                    newPassword = state.value.newPassword
                )
            }

            is ChangePasswordContractEvent.OnEnterNewPassword -> {
                _state.update { oldState ->
                    oldState.copy(
                        newPassword = event.value
                    )
                }
            }

            is ChangePasswordContractEvent.OnEnterCurrentPassword -> {
                _state.update { oldState ->
                    oldState.copy(
                        currentPassword = event.value
                    )
                }
            }

            ChangePasswordContractEvent.OnBackClicked -> {
                _channel.trySend(
                    ChangePasswordContractChannel.NavigateBack
                )
            }
        }
    }

    private fun onPrimaryButtonClicked(
        currentPassword: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
                _channel.send(
                    ChangePasswordContractChannel.ShowMessage(
                        MessageContent.Snackbar.MessageContentRes(
                            titleRes = R.string.ChangePassword_Message_Error_title,
                            subtitleRes = R.string.ChangePassword_Message_Error_empty,
                            type = MessageType.Error
                        )
                    )
                )
                return@launch
            } else if (newPassword.length <= 5) {
                _channel.send(
                    ChangePasswordContractChannel.ShowMessage(
                        MessageContent.Snackbar.MessageContentRes(
                            titleRes = R.string.ChangePassword_Message_Error_title,
                            subtitleRes = R.string.ChangePassword_Message_Error_length,
                            type = MessageType.Error
                        )
                    )
                )
                return@launch
            }
            updateButtonState(ButtonState.Loading)
            val credential = EmailAuthProvider.getCredential(
                currentUser?.email ?: "",
                currentPassword
            )
            currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser?.updatePassword(newPassword)?.addOnCompleteListener {
                        viewModelScope.launch {
                            if (task.isSuccessful) {
                                authorizationRepository.setUserInfo(
                                    password = newPassword
                                )
                                _channel.send(
                                    ChangePasswordContractChannel.ShowMessage(
                                        MessageContent.Snackbar.MessageContentRes(
                                            titleRes = R.string.ChangePassword_Message_Success_title,
                                            subtitleRes = R.string.ChangePassword_Message_Success_passwordChange,
                                            type = MessageType.Success
                                        )
                                    )
                                )
                                updateButtonState(ButtonState.Normal)
                                delay(1000)
                                _channel.trySend(ChangePasswordContractChannel.NavigateBack)
                            } else {
                                it.exception?.mapToFailure()?.sendException()
                                updateButtonState(ButtonState.Normal)
                            }
                        }
                    }
                } else {
                    task.exception?.mapToFailure()?.sendException()
                }
            }
        }
    }

    private fun updateButtonState(buttonState: ButtonState) {
        _state.update { oldState ->
            oldState.copy(
                primaryButtonState = buttonState
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(ChangePasswordContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class ChangePasswordContractState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val primaryButtonState: ButtonState = ButtonState.Normal
)

@Immutable
sealed interface ChangePasswordContractEvent {
    object OnBackClicked : ChangePasswordContractEvent
    object OnClickPrimaryButton : ChangePasswordContractEvent

    data class OnEnterCurrentPassword(
        val value: String
    ) : ChangePasswordContractEvent

    data class OnEnterNewPassword(
        val value: String
    ) : ChangePasswordContractEvent
}

@Immutable
sealed interface ChangePasswordContractChannel {
    object NavigateBack : ChangePasswordContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ChangePasswordContractChannel
}