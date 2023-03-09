package abm.co.feature.welcomelogin

import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
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
) : ViewModel(), WelcomeLoginContract {

    private val mutableState = MutableStateFlow(WelcomeLoginContract.State())
    override val state: StateFlow<WelcomeLoginContract.State> = mutableState.asStateFlow()

    private val _channel = Channel<WelcomeLoginContract.Channel>()
    override val channel = _channel.receiveAsFlow()

    override fun event(event: WelcomeLoginContract.Event) {
        viewModelScope.launch {
            when (event) {
                WelcomeLoginContract.Event.OnClickLogin -> {
                    _channel.send(WelcomeLoginContract.Channel.NavigateToLoginPage)
                }
                WelcomeLoginContract.Event.OnClickLoginAsGuest -> {
//                    loginAnonymously()
                }
                WelcomeLoginContract.Event.OnClickSignUp -> {
                    _channel.send(WelcomeLoginContract.Channel.NavigateToSignUpPage)
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
            navigateToHomePage()
        }
    }

    private fun navigateToHomePage() {
        viewModelScope.launch {
            _channel.send(WelcomeLoginContract.Channel.NavigateToHomePage)
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(WelcomeLoginContract.Channel.ShowMessage(it))
            }
        }
    }
}
