package abm.co.feature.card.setofcards

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class EditSetOfCardsViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(EditSetOfCardsContractState())
    val state: StateFlow<EditSetOfCardsContractState> = mutableState.asStateFlow()

    fun event(event: EditSetOfCardsContractEvent) = when (event) {

        else -> {}
    }
}

data class EditSetOfCardsContractState(
    val showFavoriteList: Boolean = false
)

sealed interface EditSetOfCardsContractEvent {
}