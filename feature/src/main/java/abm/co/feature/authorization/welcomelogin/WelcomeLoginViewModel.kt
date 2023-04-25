package abm.co.feature.authorization.welcomelogin

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
class WelcomeLoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val mutableState = MutableStateFlow(WelcomeLoginContractState())
    val state: StateFlow<WelcomeLoginContractState> = mutableState.asStateFlow()

    private val _channel = Channel<WelcomeLoginContractChannel>()
    val channel = _channel.receiveAsFlow()

    fun event(event: WelcomeLoginContractEvent) {
        viewModelScope.launch {
            when (event) {
                WelcomeLoginContractEvent.OnClickLogin -> {
                    _channel.send(WelcomeLoginContractChannel.NavigateToLoginPage)
                }
                WelcomeLoginContractEvent.OnClickLoginAsGuest -> {
                    loginAnonymously()
                }
                WelcomeLoginContractEvent.OnClickSignUp -> {
                    _channel.send(WelcomeLoginContractChannel.NavigateToSignUpPage)
                }
            }
        }
    }

    private fun loginAnonymously() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(200) // not needed delay
            mutableState.update { it.copy(isLoading = true) }
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserExistence()
                    } else {
                        task.exception?.mapToFailure()?.sendException()
                    }
                    mutableState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun checkUserExistence(currentUser: FirebaseUser? = firebaseAuth.currentUser) {
        mutableState.update { it.copy(isLoading = false) }
        if (currentUser != null) {
            navigateToChooseUserAttributes()
        }
    }

    private fun navigateToChooseUserAttributes() {
        viewModelScope.launch {
            _channel.send(WelcomeLoginContractChannel.NavigateToChooseUserAttributes)
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(WelcomeLoginContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class WelcomeLoginContractState(val isLoading: Boolean = false)

@Immutable
sealed interface WelcomeLoginContractEvent {
    object OnClickLogin : WelcomeLoginContractEvent
    object OnClickSignUp : WelcomeLoginContractEvent
    object OnClickLoginAsGuest : WelcomeLoginContractEvent
}

@Immutable
sealed interface WelcomeLoginContractChannel {
    object NavigateToChooseUserAttributes : WelcomeLoginContractChannel
    object NavigateToLoginPage : WelcomeLoginContractChannel
    object NavigateToSignUpPage : WelcomeLoginContractChannel
    data class ShowMessage(val messageContent: MessageContent): WelcomeLoginContractChannel
}