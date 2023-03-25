package abm.co.feature.game.swipe

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.game.swipe.drag.DraggableSide
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SwipeGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val category: CategoryUI? = savedStateHandle["category"]

    private val _channel = Channel<SwipeGameContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val cardList = mutableStateListOf<CardUI>()

    private val mutableState: MutableStateFlow<SwipeGameContractState> =
        MutableStateFlow(SwipeGameContractState.Loading(category?.name ?: ""))
    val state: StateFlow<SwipeGameContractState> = mutableState.asStateFlow()

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
            cardList.addAll(
                buildList {
                    repeat(500){
                        add(
                            CardUI(
                                name = "$it",
                                translations = "",
                                imageUrl = "",
                                examples = "",
                                kind = CardKindUI.UNKNOWN,
                                categoryID = "",
                                repeatCount = 0,
                                nextRepeatTime = 0,
                                cardID = "$it"
                            )
                        )
                    }
                }
            )
            mutableState.value = SwipeGameContractState.Success(
                _categoryName = category?.name ?: "",
                cards = cardList
            )
        }
    }

    private fun onClickOrSwipeSide(draggableSide: DraggableSide) {
        when (draggableSide) {
            DraggableSide.BOTTOM -> {
                cardList.removeFirstOrNull()
            }
            DraggableSide.END -> {
                cardList.removeFirstOrNull()
            }
            DraggableSide.START -> {
                cardList.removeFirstOrNull()
            }
            DraggableSide.TOP -> Unit
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

@Stable
sealed interface SwipeGameContractEvent {
    @Immutable
    object OnBack : SwipeGameContractEvent

    @Immutable
    data class OnSwipeOrClick(val kind: DraggableSide) : SwipeGameContractEvent
}

@Stable
sealed interface SwipeGameContractChannel {
    @Immutable
    object OnBack : SwipeGameContractChannel

    @Immutable
    data class ShowMessage(val messageContent: MessageContent) : SwipeGameContractChannel
}

