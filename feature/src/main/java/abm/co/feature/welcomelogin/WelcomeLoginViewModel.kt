package abm.co.feature.welcomelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WelcomeLoginViewModel @Inject constructor(
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
                    _channel.send(WelcomeLoginContract.Channel.NavigateToHomePage)
                }
                WelcomeLoginContract.Event.OnClickRegistration -> {
                    _channel.send(WelcomeLoginContract.Channel.NavigateToRegistrationPage)
                }
            }
        }
    }
}
