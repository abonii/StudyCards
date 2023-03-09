package abm.co.feature.signup

import abm.co.designsystem.UnidirectionalViewModel
import abm.co.designsystem.message.common.MessageContent
import abm.co.domain.base.Failure
import android.content.Intent
import androidx.annotation.StringRes

interface SignUpContract :
    UnidirectionalViewModel<SignUpContract.Event, SignUpContract.State, SignUpContract.Channel> {

    data class State(
        val isLoginButtonLoading: Boolean = false,
        val isGoogleButtonLoading: Boolean = false,
        val isFacebookButtonLoading: Boolean = false,
        @StringRes val errorRes: Int? = null,
        val email: String = "",
        val password: String = "",
        val passwordConfirm: String = ""
    )

    sealed interface Event {
        object OnSignUpViaEmailClicked : Event
        object OnLoginViaGoogleClicked : Event
        object OnLoginViaFacebookClicked : Event
        object OnLoginClicked : Event
        data class OnEnterEmailValue(val value: String) : Event
        data class OnEnterPasswordValue(val value: String) : Event
        data class OnEnterPasswordConfirmValue(val value: String) : Event
    }

    sealed interface Channel {
        data class LoginViaGoogle(val intent: Intent) : Channel
        object NavigateToLogin : Channel
        object NavigateToHome : Channel
        data class ShowMessage(val messageContent: MessageContent) : Channel
    }
}
