package abm.co.feature.registration

import abm.co.designsystem.UnidirectionalViewModel
import androidx.compose.ui.graphics.Color

interface RegistrationContract :
    UnidirectionalViewModel<RegistrationContract.Event, RegistrationContract.State> {

    data class State(
        val color: Color,
        val refreshing: Boolean = false,
        val showFavoriteList: Boolean = false,
    )

    sealed class Event {
        object OnRefresh : Event()
    }
}
