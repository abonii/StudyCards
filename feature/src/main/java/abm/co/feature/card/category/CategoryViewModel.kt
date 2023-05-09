package abm.co.feature.card.category

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CardItemUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toItemUI
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

    private val mutableScreenState =
        MutableStateFlow<CategoryContract.ScreenState>(CategoryContract.ScreenState.Loading)
    val screenState: StateFlow<CategoryContract.ScreenState> = mutableScreenState.asStateFlow()

    private val mutableToolbarState = MutableStateFlow(
        CategoryContract.ToolbarState(
            categoryName = category.name,
            description = "Добавленные слова" // TODO store it in strings
        )
    )
    val toolbarState: StateFlow<CategoryContract.ToolbarState> = mutableToolbarState.asStateFlow()

    private val cardItems = mutableStateListOf<CardItemUI>()

    init {
//        viewModelScope.launch {
//            serverRepository.createCard(
//                Card(
//                    name = "name",
//                    translations = "translations",
//                    imageUrl = "",
//                    examples = "examples",
//                    kind = CardKind.KNOWN,
//                    categoryID = category.id,
//                    repeatCount = 0,
//                    nextRepeatTime = System.currentTimeMillis(),
//                    id = ""
//                )
//            )
//        }
        fetchCardItems()
    }

    fun event(event: CategoryContractEvent) {
        when (event) {
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
        }
    }

    private fun fetchCardItems() {
        viewModelScope.launch {
            serverRepository.getCards(category.id)
                .collectLatest { either ->
                    either.onSuccess { items ->
                        when (screenState.value) {
                            is CategoryContract.ScreenState.Success -> {
                                cardItems.clear()
                                cardItems.addAll(items.map { it.toItemUI() })
                            }

                            else -> {
                                cardItems.addAll(items.map { it.toItemUI() })
                                mutableScreenState.value = CategoryContract.ScreenState.Success(
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
        val description: String
    ) : CategoryContract

    @Stable
    sealed interface ScreenState : CategoryContract {
        @Immutable
        object Loading : ScreenState

        @Immutable
        object Empty : ScreenState

        @Immutable
        data class Success(
            val cards: List<CardItemUI>
        ) : ScreenState
    }
}

@Stable
sealed interface CategoryContractEvent {

    @Immutable
    data class OnClickPlayCard(val cardItem: CardItemUI) : CategoryContractEvent

    @Immutable
    object OnClickNewCard : CategoryContractEvent

    @Immutable
    object OnClickEditCategory : CategoryContractEvent

    @Immutable
    data class OnClickCardItem(val cardItem: CardItemUI) : CategoryContractEvent

    @Immutable
    object OnBackClicked : CategoryContractEvent
}

@Stable
sealed interface CategoryContractChannel {

    @Immutable
    object NavigateBack : CategoryContractChannel

    @Immutable
    data class NavigateToCard(
        val cardItem: CardItemUI?,
        val category: CategoryUI
    ) : CategoryContractChannel

    @Immutable
    object NavigateToChooseOrCreateCategory : CategoryContractChannel

    @Immutable
    data class ShowMessage(val messageContent: MessageContent) : CategoryContractChannel
}