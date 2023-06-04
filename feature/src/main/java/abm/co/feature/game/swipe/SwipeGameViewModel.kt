package abm.co.feature.game.swipe

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.model.CardKind
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.card.model.toUI
import abm.co.feature.game.swipe.drag.DraggableSide
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwipeGameViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val category: CategoryUI = savedStateHandle["category"]
        ?: throw RuntimeException("cannot be empty CATEGORY argument")

    private val _channel = Channel<SwipeGameContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val cardList = mutableStateListOf<CardUI>()

    private val _state: MutableStateFlow<SwipeGameContractState> =
        MutableStateFlow(SwipeGameContractState.Loading(category.title))
    val state: StateFlow<SwipeGameContractState> = _state.asStateFlow()

    init {
        fetchCards()
    }

    fun event(event: SwipeGameContractEvent) {
        when (event) {
            SwipeGameContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(SwipeGameContractChannel.OnBack)
                }
            }

            is SwipeGameContractEvent.OnSwipeOrClick -> {
                onClickOrSwipeSide(event.kind)
            }
        }
    }

    private fun fetchCards() {
        viewModelScope.launch {
            delay(1000) // in explore copy not
            serverRepository.getUserCards(category.id).firstOrNull()
                ?.onSuccess { cards ->
                    cardList.addAll(
                        cards.filter {
                            it.kind == CardKind.UNDEFINED
                        }.map {
                            it.toUI()
                        }
                    )
                    listenToCardListEmptiness()
                }?.onFailure {
                    it.sendException()
                }
            _state.value = SwipeGameContractState.Success(
                _categoryName = category.title,
                cards = cardList
            )
        }
    }

    private fun listenToCardListEmptiness() {
        snapshotFlow {
            cardList.size
        }.onEach {
            if (it == 0) {
                _channel.send(SwipeGameContractChannel.OnFinish)
            }
        }.launchIn(viewModelScope)
    }

    private fun onClickOrSwipeSide(draggableSide: DraggableSide) {
        when (draggableSide) {
            DraggableSide.BOTTOM -> {
                cardList.removeFirstOrNull()?.let {
                    updateCardKind(card = it, kindUI = CardKindUI.UNCERTAIN)
                }
            }

            DraggableSide.END -> {
                cardList.removeFirstOrNull()?.let {
                    updateCardKind(card = it, kindUI = CardKindUI.KNOWN)
                }
            }

            DraggableSide.START -> {
                cardList.removeFirstOrNull()?.let {
                    updateCardKind(card = it, kindUI = CardKindUI.UNKNOWN)
                }
            }

            DraggableSide.TOP -> Unit
        }
    }

    private fun updateCardKind(card: CardUI, kindUI: CardKindUI) {
        viewModelScope.launch {
            serverRepository.updateUserCardKind(
                cardID = card.cardID,
                categoryID = card.categoryID,
                kind = kindUI.toDomain()
            )
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(SwipeGameContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
sealed class SwipeGameContractState(val categoryName: String) {
    @Immutable
    data class Loading(
        val _categoryName: String
    ) : SwipeGameContractState(_categoryName)

    @Immutable
    data class Success(
        val _categoryName: String,
        val cards: List<CardUI>
    ) : SwipeGameContractState(_categoryName)
}

@Immutable
sealed interface SwipeGameContractEvent {
    object OnBack : SwipeGameContractEvent

    data class OnSwipeOrClick(val kind: DraggableSide) : SwipeGameContractEvent
}

@Immutable
sealed interface SwipeGameContractChannel {
    object OnBack : SwipeGameContractChannel
    object OnFinish : SwipeGameContractChannel

    data class ShowMessage(val messageContent: MessageContent) : SwipeGameContractChannel
}
