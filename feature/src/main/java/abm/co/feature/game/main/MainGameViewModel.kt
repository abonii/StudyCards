package abm.co.feature.game.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class MainGameViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(MainGameContractState())
    val state: StateFlow<MainGameContractState> = mutableState.asStateFlow()

    fun event(event: MainGameContractEvent) = when (event) {

        else -> {}
    }
}

data class MainGameContractState(
    val showFavoriteList: Boolean = false
)

sealed interface MainGameContractEvent {
}