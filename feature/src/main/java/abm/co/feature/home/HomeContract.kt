package abm.co.feature.home

import abm.co.designsystem.UnidirectionalViewModel
import androidx.compose.ui.graphics.Color

interface HomeContract :
    UnidirectionalViewModel<HomeContract.Event, HomeContract.State, Nothing> {

    data class State(
        val showFavoriteList: Boolean = false
    )

    sealed class Event {
        object OnRefresh : Event()
    }
}
