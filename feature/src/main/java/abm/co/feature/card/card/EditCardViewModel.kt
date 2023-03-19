package abm.co.feature.card.card

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class EditCardViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(EditCardContractState())
    val state: StateFlow<EditCardContractState> = mutableState.asStateFlow()

    fun event(event: EditCardContractEvent) = when (event) {

        else -> {}
    }
}

data class EditCardContractState(
    val showFavoriteList: Boolean = false
)

sealed interface EditCardContractEvent {
}