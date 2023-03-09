package abm.co.feature.login

import abm.co.designsystem.UnidirectionalViewModel
import abm.co.designsystem.message.common.MessageContent
import android.content.Intent

interface LoginContract :
    UnidirectionalViewModel<LoginContract.Event, LoginContract.State, LoginContract.Channel> {

    data class State(
        val isLoginButtonLoading: Boolean = false,
        val isGoogleButtonLoading: Boolean = false,
        val isFacebookButtonLoading: Boolean = false,
        val email: String = "",
        val password: String = ""
    )

    sealed interface Event {
        object OnLoginViaEmailClicked : Event
        object OnLoginViaGoogleClicked : Event
        object OnLoginViaFacebookClicked : Event
        object OnSignUpClicked : Event
        data class OnEnterEmailValue(val value: String) : Event
        data class OnEnterPasswordValue(val value: String) : Event
    }

    sealed interface Channel {
        data class LoginViaGoogle(val intent: Intent) : Channel
        object NavigateToSignUp : Channel
        object NavigateToHome : Channel
        object NavigateToForgotPassword : Channel
        data class ShowMessage(val messageContent: MessageContent) : Channel
    }
}
