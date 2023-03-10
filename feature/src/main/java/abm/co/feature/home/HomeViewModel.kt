package abm.co.feature.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(HomeContractState())
    val state: StateFlow<HomeContractState> = mutableState.asStateFlow()

    fun event(event: HomeContractEvent) = when (event) {
        HomeContractEvent.OnRefresh -> {

        }
    }
}

data class HomeContractState(
    val showFavoriteList: Boolean = false
)

sealed interface HomeContractEvent {
    object OnRefresh : HomeContractEvent
}