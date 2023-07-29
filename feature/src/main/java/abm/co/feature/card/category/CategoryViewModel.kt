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

    private var category: CategoryUI = savedStateHandle["category"]
        ?: throw RuntimeException("cannot be empty CATEGORY argument")

    private val _channel = Channel<CategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow<CategoryContract.ScreenState>(
        CategoryContract.ScreenState.Loading
    )
    val state: StateFlow<CategoryContract.ScreenState> = _state.asStateFlow()

    private val _toolbarState = MutableStateFlow(
        CategoryContract.ToolbarState(
            categoryTitle = category.title,
            description = category.description,
            cardsCount = category.cardsCount,
            imageUrl = category.imageURL
        )
    )
    val toolbarState: StateFlow<CategoryContract.ToolbarState> = _toolbarState.asStateFlow()

    private val _dialogState = MutableStateFlow(CategoryContract.Dialog())
    val dialogState: StateFlow<CategoryContract.Dialog> = _dialogState.asStateFlow()

    private val cardItems = mutableStateListOf<CardUI>()

    init {
        fetchCardItems()
    }

    fun onEvent(event: CategoryContractEvent) {
        when (event) {
            is CategoryContractEvent.Success.OnClickPlayCard -> {
                speak(event.cardItem.translation)
            }

            CategoryContractEvent.Toolbar.OnClickNewCard -> {
                viewModelScope.launch {
                    _channel.send(
                        CategoryContractChannel.NavigateToCard(
                            cardItem = null,
                            category = category
                        )
                    )
                }
            }

            CategoryContractEvent.Toolbar.OnBack -> {
                viewModelScope.launch {
                    _channel.send(CategoryContractChannel.NavigateBack)
                }
            }

            is CategoryContractEvent.Success.OnClickCardItem -> {
                viewModelScope.launch {
                    _channel.send(
                        CategoryContractChannel.NavigateToCard(
                            cardItem = event.cardItem,
                            category = category
                        )
                    )
                }
            }

            CategoryContractEvent.Toolbar.OnClickEditCategory -> {
                viewModelScope.launch {
                    _channel.send(
                        CategoryContractChannel.NavigateToChangeCategory(
                            category = category
                        )
                    )
                }
            }

            is CategoryContractEvent.Success.OnLongClickCard -> {
                _dialogState.update { oldState ->
                    oldState.copy(removingCard = event.cardItem)
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
                _dialogState.update { oldState ->
                    oldState.copy(removingCard = null)
                }
            }

            CategoryContractEvent.Toolbar.OnClickShare -> {
                // todo
            }
        }
    }

    private fun fetchCardItems() {
        viewModelScope.launch {
            serverRepository.getUserCards(category.id)
                .collectLatest { either ->
                    either.onSuccess { items ->
                        if (items.isEmpty()) {
                            _state.value = CategoryContract.ScreenState.Empty
                        } else {
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

    fun onSelectedCategory(category: CategoryUI) {
        this.category = category
        _toolbarState.update { oldState ->
            oldState.copy(
                categoryTitle = category.title
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent().let {
                _channel.send(CategoryContractChannel.ShowMessage(it))
            }
        }
    }

    override fun onCleared() {
        textToSpeechManager.clear()
        super.onCleared()
    }
}

@Stable
sealed interface CategoryContract {

    @Immutable
    data class ToolbarState(
        val imageUrl: String?,
        val categoryTitle: String,
        val description: String,
        val cardsCount: Int
    ) : CategoryContract

    @Stable
    sealed interface ScreenState : CategoryContract {
        @Immutable
        object Loading : ScreenState

        @Immutable
        object Empty : ScreenState

        @Immutable
        data class Success(
            val cards: List<CardUI>
        ) : ScreenState
    }

    data class Dialog(
        val removingCard: CardUI? = null
    ) : CategoryContract
}

@Immutable
sealed interface CategoryContractEvent {

    data class OnConfirmRemoveCard(val cardItem: CardUI) : CategoryContractEvent

    object OnDismissDialog : CategoryContractEvent

    sealed interface Success: CategoryContractEvent {
        data class OnClickPlayCard(val cardItem: CardUI) : Success

        data class OnClickCardItem(val cardItem: CardUI) : Success

        data class OnLongClickCard(val cardItem: CardUI) : Success

    }

    sealed interface Toolbar : CategoryContractEvent {
        object OnBack : Toolbar
        object OnClickNewCard : Toolbar
        object OnClickEditCategory : Toolbar
        object OnClickShare : Toolbar
    }
}

@Immutable
sealed interface CategoryContractChannel {

    object NavigateBack : CategoryContractChannel

    data class NavigateToChangeCategory(
        val category: CategoryUI
    ) : CategoryContractChannel

    data class NavigateToCard(
        val cardItem: CardUI?,
        val category: CategoryUI
    ) : CategoryContractChannel

    data class ShowMessage(val messageContent: MessageContent) : CategoryContractChannel
}