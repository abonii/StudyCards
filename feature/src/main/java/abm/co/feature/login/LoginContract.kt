package abm.co.feature.login

import abm.co.designsystem.UnidirectionalViewModel
import abm.co.domain.base.Failure
import android.content.Intent
import androidx.annotation.StringRes

interface LoginContract :
    UnidirectionalViewModel<LoginContract.Event, LoginContract.State, LoginContract.Channel> {

    data class State(
        val isLoading: Boolean = false,
        @StringRes val errorRes: Int? = null,
        val email: String = "",
        val password: String = ""
    )

    sealed interface Event {
        object LoginViaGoogle : Event
    }

    sealed interface Channel {
        data class ShowError(val failure: Failure) : Channel
        data class LoginViaGoogle(val intent: Intent) : Channel
        object NavigateToRegistration : Channel
        object NavigateToHome : Channel
        object NavigateToForgotPassword : Channel
        object NavigateToEmailFragment : Channel
    }
}
