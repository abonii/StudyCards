package abm.co.feature.card.category

import abm.co.domain.model.Category
import abm.co.domain.repository.ServerRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _channel = Channel<CategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val mutableState = MutableStateFlow(CategoryContractState())
    val state: StateFlow<CategoryContractState> = mutableState.asStateFlow()

    fun event(event: CategoryContractEvent) {
        when (event) {
            is CategoryContractEvent.OnEnterCategoryName -> {
                mutableState.update {
                    it.copy(categoryName = event.value)
                }
            }
            CategoryContractEvent.OnContinueButtonClicked -> {
               onContinueButtonClicked()
            }
            CategoryContractEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _channel.send(CategoryContractChannel.NavigateBack)
                }
            }
        }
    }

    private fun onContinueButtonClicked() {
        viewModelScope.launch {
            serverRepository.createCategory(
                Category(
                    name = state.value.categoryName,
                    cardsCount = 0,
                    bookmarked = false,
                    creatorName = null,
                    creatorID = null,
                    imageURL = null,
                    id = ""
                )
            )
            _channel.send(CategoryContractChannel.NavigateToNewCard)
        }
    }
}

data class CategoryContractState(
    val categoryName: String = ""
)

sealed interface CategoryContractEvent {
    data class OnEnterCategoryName(val value: String) : CategoryContractEvent
    object OnContinueButtonClicked : CategoryContractEvent
    object OnBackClicked : CategoryContractEvent
}

sealed interface CategoryContractChannel {
    object NavigateBack : CategoryContractChannel
    object NavigateToNewCard : CategoryContractChannel
}