package abm.co.feature.game.picker

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import abm.co.feature.game.model.GameKindUI
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamePickerViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val category: CategoryUI? get() = savedStateHandle["category"]

    private val _channel = Channel<GamePickerContractChannel>()
    val channel: Flow<GamePickerContractChannel> get() = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(GamePickerContractState())
    val state: StateFlow<GamePickerContractState> = _state.asStateFlow()

    private var allCards = emptyList<CardUI>()

    init {
        fetchAllCards()
    }

    private fun fetchAllCards() {
        viewModelScope.launch {
            serverRepository.getUserCards(categoryID = category?.id ?: return@launch)
                .collectLatest { either ->
                    either.onSuccess { items ->
                        allCards = items.map { it.toUI() }
                        _state.update { oldState ->
                            oldState.copy(
                                cardsToLearn = allCards.filter { it.kind == CardKindUI.UNDEFINED }.size,
                                cardsToRepeat = allCards.filter {
                                    it.kind != CardKindUI.UNDEFINED &&
                                            it.kind != CardKindUI.KNOWN &&
                                            System.currentTimeMillis() > it.nextRepeatTime
                                }.size,
                                allCards = allCards.size
                            )
                        }
                    }.onFailure {
                        it.sendException()
                    }
                }
        }
    }

    fun onEvent(event: GamePickerContractEvent) = when (event) {
        is GamePickerContractEvent.OnGamePicked -> {
            viewModelScope.launch {
                _channel.send(
                    GamePickerContractChannel.NavigateToGame(
                        cards = allCards.filter { it.kind != CardKindUI.UNDEFINED },
                        gameKind = event.gameKind
                    )
                )
            }
        }

        GamePickerContractEvent.OnLearnPicked -> {
            viewModelScope.launch {
                category?.let {
                    _channel.send(
                        GamePickerContractChannel.NavigateToLearn(
                            category = it
                        )
                    )
                }
            }
        }

        GamePickerContractEvent.OnOneGameToggled -> {
            viewModelScope.launch {
                _state.update { oldState ->
                    oldState.copy(
                        oneGameExpanded = !oldState.oneGameExpanded
                    )
                }
            }
        }

        GamePickerContractEvent.OnRepeatPicked -> {
            viewModelScope.launch {
                _channel.send(
                    GamePickerContractChannel.NavigateToRepeat(
                        cards = allCards.filter {
                            System.currentTimeMillis() > it.nextRepeatTime
                        }
                    )
                )
            }
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(GamePickerContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class GamePickerContractState(
    val cardsToLearn: Int = 0,
    val cardsToRepeat: Int = 0,
    val allCards: Int = 0,
    val oneGameExpanded: Boolean = false
)

@Immutable
sealed interface GamePickerContractEvent {
    object OnLearnPicked : GamePickerContractEvent
    object OnRepeatPicked : GamePickerContractEvent
    object OnOneGameToggled : GamePickerContractEvent
    data class OnGamePicked(val gameKind: GameKindUI) : GamePickerContractEvent
}

@Immutable
sealed interface GamePickerContractChannel {
    data class NavigateToLearn(val category: CategoryUI) : GamePickerContractChannel
    data class NavigateToRepeat(val cards: List<CardUI>) : GamePickerContractChannel
    data class ShowMessage(val messageContent: MessageContent) : GamePickerContractChannel
    data class NavigateToGame(
        val gameKind: GameKindUI,
        val cards: List<CardUI>
    ) : GamePickerContractChannel
}
