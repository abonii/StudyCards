package abm.co.feature.game.guess

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.guess.model.GuessItemUI
import abm.co.feature.game.guess.model.toGuessItems
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cards: Array<CardUI> = savedStateHandle["cards"]
        ?: throw RuntimeException("cannot be empty CARDS argument")

    private val _channel = Channel<GuessContractChannel>()
    val channel = _channel.receiveAsFlow()

    val state = GuessContractState()

    val items by lazy {
        cards.toGuessItems().takeIf { it.isNotEmpty() }
            ?: throw RuntimeException("must not be empty CARDS array")
    }

    init {
        setupCards()
    }

    fun onEvent(event: GuessContractEvent) {
        when (event) {
            GuessContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(
                        GuessContractChannel.NavigateBack
                    )
                }
            }

            is GuessContractEvent.OnSelectAnswer -> {
                viewModelScope.launch {
                    if (event.answer.cardID == state.item.value?.cardID) {
                        val currentIndex = items.indexOfFirst {
                            state.item.value?.cardID == it.cardID
                        }
                        if (currentIndex == -1) return@launch
                        if (currentIndex + 1 >= items.lastIndex) {
                            _channel.send(GuessContractChannel.Finished)
                            return@launch
                        }
                        state.updateItem(newItem = items[currentIndex + 1])
                    } else {
                        state.item.value?.let {
                            state.updateItem(
                                newItem = it.copy(
                                    answers = it.answers.shuffled()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupCards() {
        viewModelScope.launch {
            state.updateItem(items.first())
        }
    }

    private fun MessageContent.sendMessage() {
        viewModelScope.launch {
            this@sendMessage.let {
                _channel.send(GuessContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
class GuessContractState {
    private val _item = mutableStateOf<GuessItemUI?>(null)
    val item: State<GuessItemUI?> = _item

    fun updateItem(newItem: GuessItemUI) {
        _item.value = newItem
    }
}

@Immutable
sealed interface GuessContractEvent {
    object OnBack : GuessContractEvent
    data class OnSelectAnswer(val answer: GuessItemUI.Answer) : GuessContractEvent
}

@Immutable
sealed interface GuessContractChannel {
    object NavigateBack : GuessContractChannel
    object Finished : GuessContractChannel
    data class ShowMessage(val messageContent: MessageContent) : GuessContractChannel
}
