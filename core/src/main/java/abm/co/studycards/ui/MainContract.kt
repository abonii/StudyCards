package abm.co.studycards.ui

import abm.co.designsystem.UnidirectionalViewModel

interface MainContract :
    UnidirectionalViewModel<MainContract.Event, MainContract.State, Nothing> {

    data class State(
        val isLoggedIn: Boolean
    )

    sealed class Event {
        object OnRefresh : Event()
    }
}
