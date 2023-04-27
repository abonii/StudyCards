package abm.co.feature.card.editcategory

import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toDomain
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _channel = Channel<EditCategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val mutableState = MutableStateFlow(EditCategoryContractState())
    val state: StateFlow<EditCategoryContractState> = mutableState.asStateFlow()

    fun event(event: EditCategoryContractEvent) {
        when (event) {
            is EditCategoryContractEvent.OnEnterCategoryName -> {
                mutableState.update {
                    it.copy(categoryName = event.value)
                }
            }

            EditCategoryContractEvent.OnContinueButtonClicked -> {
                onContinueButtonClicked()
            }

            EditCategoryContractEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _channel.send(EditCategoryContractChannel.NavigateBack)
                }
            }
        }
    }

    private fun onContinueButtonClicked() {
        viewModelScope.launch {
            val category = CategoryUI(
                name = state.value.categoryName,
                cardsCount = 0,
                bookmarked = false,
                creatorName = null,
                creatorID = null,
                imageURL = null,
                id = ""
            )
            serverRepository.createCategory(category.toDomain())
            _channel.send(EditCategoryContractChannel.NavigateToNewCard(category = category))
        }
    }
}

data class EditCategoryContractState(
    val categoryName: String = ""
)

@Stable
sealed interface EditCategoryContractEvent {
    data class OnEnterCategoryName(val value: String) : EditCategoryContractEvent
    object OnContinueButtonClicked : EditCategoryContractEvent
    object OnBackClicked : EditCategoryContractEvent
}

@Immutable
sealed interface EditCategoryContractChannel {
    object NavigateBack : EditCategoryContractChannel
    data class NavigateToNewCard(val category: CategoryUI) : EditCategoryContractChannel
}