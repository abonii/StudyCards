package abm.co.feature.game

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class GameViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(GameContractState())
    val state: StateFlow<GameContractState> = mutableState.asStateFlow()

    fun event(event: GameContractEvent) = when (event) {

        else -> {}
    }
}

data class GameContractState(
    val showFavoriteList: Boolean = false
)

sealed interface GameContractEvent {
}