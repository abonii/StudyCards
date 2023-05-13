package abm.co.feature.card.category

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import abm.co.feature.utils.TextToSpeechManager
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
    private val textToSpeechManager: TextToSpeechManager,
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

    private val _toolbarState = MutableStateFlow(
        CategoryContract.ToolbarState(
            categoryName = category.name,
            descriptionRes = R.string.Category_Toolbar_subtitle
        )
    )
    val toolbarState: StateFlow<CategoryContract.ToolbarState> = _toolbarState.asStateFlow()

    private val cardItems = mutableStateListOf<CardUI>()

    init {
        fetchCardItems()
    }

    fun onEvent(event: CategoryContractEvent) {
        when (event) {
            is CategoryContractEvent.OnClickPlayCard -> {
                speak(event.cardItem.translation)
            }

            CategoryContractEvent.OnClickNewCard -> {
                viewModelScope.launch {
                    _channel.trySend(
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
                            cardItem = event.cardItem,
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
                        ?.copy(removingCard = event.cardItem) ?: oldState
                }
            }

            is CategoryContractEvent.OnConfirmRemoveCard -> {
                viewModelScope.launch {
                    onEvent(CategoryContractEvent.OnDismissDialog)
                    serverRepository.removeUserCard(
                        categoryID = event.cardItem.categoryID,
                        cardID = event.cardItem.cardID
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

    private fun speak(text: String) {
        viewModelScope.launch {
            val canSpeak = textToSpeechManager.speakAndGet(text)
            if (!canSpeak) {
                _channel.send(
                    CategoryContractChannel.ShowMessage(
                        MessageContent.Snackbar.MessageContentRes(
                            titleRes = abm.co.designsystem.R.string.Messages_oops,
                            subtitleRes = R.string.Category_Message_Error_TextToSpeech_notFound,
                            type = MessageType.Error
                        )
                    )
                )
            }
        }
    }

    override fun onCleared() {
        textToSpeechManager.clear()
        super.onCleared()
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

@Immutable
sealed interface CategoryContractEvent {

    data class OnClickPlayCard(val cardItem: CardUI) : CategoryContractEvent

    object OnClickNewCard : CategoryContractEvent

    object OnClickEditCategory : CategoryContractEvent

    data class OnClickCardItem(val cardItem: CardUI) : CategoryContractEvent

    object OnBackClicked : CategoryContractEvent

    data class OnLongClickCard(val cardItem: CardUI) : CategoryContractEvent

    data class OnConfirmRemoveCard(val cardItem: CardUI) : CategoryContractEvent

    object OnDismissDialog : CategoryContractEvent
}

@Immutable
sealed interface CategoryContractChannel {

    object NavigateBack : CategoryContractChannel

    data class NavigateToCard(
        val cardItem: CardUI?,
        val category: CategoryUI
    ) : CategoryContractChannel

    object NavigateToChooseOrCreateCategory : CategoryContractChannel

    data class ShowMessage(val messageContent: MessageContent) : CategoryContractChannel
}