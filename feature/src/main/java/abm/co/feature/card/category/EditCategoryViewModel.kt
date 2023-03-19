package abm.co.feature.card.category

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
) : ViewModel() {

    private val _channel = Channel<EditCategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val mutableState = MutableStateFlow(EditCategoryContractState())
    val state: StateFlow<EditCategoryContractState> = mutableState.asStateFlow()

    fun event(event: EditCategoryContractEvent) = when (event) {
        is EditCategoryContractEvent.OnEnterCategoryName -> {
            mutableState.update {
                it.copy(categoryName = event.value)
            }
        }
    }
}

data class EditCategoryContractState(
    val categoryName: String = ""
)

sealed interface EditCategoryContractEvent {
    data class OnEnterCategoryName(val value: String): EditCategoryContractEvent
}

sealed interface EditCategoryContractChannel {
    object NavigateBack: EditCategoryContractChannel
    object NavigateToNewCard: EditCategoryContractChannel
}