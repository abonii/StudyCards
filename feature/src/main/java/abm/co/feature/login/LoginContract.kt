package abm.co.feature.login

import abm.co.designsystem.UnidirectionalViewModel
import abm.co.feature.registration.RegistrationContract
import androidx.compose.ui.graphics.Color

interface LoginContract :
    UnidirectionalViewModel<LoginContract.Event, LoginContract.State> {

    data class State(
        val color: Color,
        val refreshing: Boolean = false,
        val showFavoriteList: Boolean = false,
    )

    sealed class Event {
        object OnRefresh : Event()
    }
}
