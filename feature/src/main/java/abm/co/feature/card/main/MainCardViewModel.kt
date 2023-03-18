package abm.co.feature.card.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class MainCardViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(MainCardContractState())
    val state: StateFlow<MainCardContractState> = mutableState.asStateFlow()

    fun event(event: MainCardContractEvent) = when (event) {

        else -> {}
    }
}

data class MainCardContractState(
    val showFavoriteList: Boolean = false
)

sealed interface MainCardContractEvent {
}