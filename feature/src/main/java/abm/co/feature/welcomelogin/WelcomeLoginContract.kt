package abm.co.feature.welcomelogin

import abm.co.designsystem.UnidirectionalViewModel
import abm.co.domain.base.Failure

interface WelcomeLoginContract :
    UnidirectionalViewModel<WelcomeLoginContract.Event, WelcomeLoginContract.State, WelcomeLoginContract.Channel> {

    data class State(val isLoading: Boolean = false)

    sealed interface Event {
        object OnClickLogin : Event
        object OnClickRegistration : Event
        object OnClickLoginAsGuest : Event
    }

    sealed interface Channel {
        object NavigateToHomePage : Channel
        object NavigateToLoginPage : Channel
        object NavigateToRegistrationPage : Channel
        data class OnFailure(val failure: Failure): Channel
    }
}
