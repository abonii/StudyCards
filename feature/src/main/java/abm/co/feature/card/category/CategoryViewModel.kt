package abm.co.feature.card.category

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val category: CategoryUI = savedStateHandle["category"]
        ?: throw RuntimeException("cannot be empty CATEGORY argument")

    private val _channel = Channel<CategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow<CategoryContract.ScreenState>(
        CategoryContract.ScreenState.Loading
    )
    val state: StateFlow<CategoryContract.ScreenState> = _state.asStateFlow()

    private val mutableToolbarState = MutableStateFlow(
        CategoryContract.ToolbarState(
            categoryName = category.name,
            descriptionRes = R.string.Category_Toolbar_subtitle
        )
    )
    val toolbarState: StateFlow<CategoryContract.ToolbarState> = mutableToolbarState.asStateFlow()

    private val cardItems = mutableStateListOf<CardUI>()

    init {
        fetchCardItems()
    }

    fun onEvent(onEvent: CategoryContractEvent) {
        when (onEvent) {
            is CategoryContractEvent.OnClickPlayCard -> {

            }

            CategoryContractEvent.OnClickNewCard -> {
                viewModelScope.launch {
                    _channel.send(
                        CategoryContractChannel.NavigateToCard(
                            cardItem = null,
                            category = category
                        )
                    )
                }
            }

            CategoryContractEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _channel.send(CategoryContractChannel.NavigateBack)
                }
            }

            is CategoryContractEvent.OnClickCardItem -> {
                viewModelScope.launch {
                    _channel.send(
                        CategoryContractChannel.NavigateToCard(
                            cardItem = onEvent.cardItem,
                            category = category
                        )
                    )
                }
            }

            CategoryContractEvent.OnClickEditCategory -> {
                viewModelScope.launch {
                    _channel.trySend(
                        CategoryContractChannel.NavigateToCard(
                            cardItem = null,
                            category = category
                        )
                    )
                }
            }

            is CategoryContractEvent.OnLongClickCard -> {
                _state.update { oldState ->
                    (oldState as? CategoryContract.ScreenState.Success)
                        ?.copy(removingCard = onEvent.cardItem) ?: oldState
                }
            }

            is CategoryContractEvent.OnConfirmRemoveCard -> {
                viewModelScope.launch {
                    onEvent(CategoryContractEvent.OnDismissDialog)
                    serverRepository.removeUserCard(
                        categoryID = onEvent.cardItem.categoryID,
                        cardID = onEvent.cardItem.cardID
                    )
                }
            }
            CategoryContractEvent.OnDismissDialog -> {
                _state.update { oldState ->
                    (oldState as? CategoryContract.ScreenState.Success)
                        ?.copy(removingCard = null) ?: oldState
                }
            }
        }
    }

    private fun fetchCardItems() {
        viewModelScope.launch {
            serverRepository.getUserCards(category.id)
                .collectLatest { either ->
                    either.onSuccess { items ->
                        when (state.value) {
                            is CategoryContract.ScreenState.Success -> {
                                cardItems.clear()
                                cardItems.addAll(items.map { it.toUI() })
                            }

                            else -> {
                                cardItems.addAll(items.map { it.toUI() })
                                _state.value = CategoryContract.ScreenState.Success(
                                    cardItems
                                )
                            }
                        }
                    }.onFailure {
                        it.sendException()
                    }
                }
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(CategoryContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
sealed interface CategoryContract {

    @Immutable
    data class ToolbarState(
        val categoryName: String,
        @StringRes val descriptionRes: Int
    ) : CategoryContract

    @Stable
    sealed interface ScreenState : CategoryContract {
        @Immutable
        object Loading : ScreenState

        @Immutable
        object Empty : ScreenState

        @Immutable
        data class Success(
            val cards: List<CardUI>,
            val removingCard: CardUI? = null
        ) : ScreenState
    }
}

@Stable
sealed interface CategoryContractEvent {

    @Immutable
    data class OnClickPlayCard(val cardItem: CardUI) : CategoryContractEvent

    @Immutable
    object OnClickNewCard : CategoryContractEvent

    @Immutable
    object OnClickEditCategory : CategoryContractEvent

    @Immutable
    data class OnClickCardItem(val cardItem: CardUI) : CategoryContractEvent

    @Immutable
    object OnBackClicked : CategoryContractEvent

    @Immutable
    data class OnLongClickCard(val cardItem: CardUI) : CategoryContractEvent

    @Immutable
    data class OnConfirmRemoveCard(val cardItem: CardUI) : CategoryContractEvent

    @Immutable
    object OnDismissDialog : CategoryContractEvent
}

@Stable
sealed interface CategoryContractChannel {

    @Immutable
    object NavigateBack : CategoryContractChannel

    @Immutable
    data class NavigateToCard(
        val cardItem: CardUI?,
        val category: CategoryUI
    ) : CategoryContractChannel

    @Immutable
    object NavigateToChooseOrCreateCategory : CategoryContractChannel

    @Immutable
    data class ShowMessage(val messageContent: MessageContent) : CategoryContractChannel
}