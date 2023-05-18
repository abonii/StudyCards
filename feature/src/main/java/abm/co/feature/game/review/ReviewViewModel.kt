package abm.co.feature.game.review

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.review.model.ReviewItemUI
import abm.co.feature.game.review.model.toReviewItem
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cards: Array<CardUI> = savedStateHandle["cards"]
        ?: throw RuntimeException("cannot be empty CARDS argument")

    private val _channel = Channel<ReviewContractChannel>()
    val channel = _channel.receiveAsFlow()

    val state = ReviewContractState()

    init {
        state.addItems(cards)
    }

    fun onEvent(event: ReviewContractEvent) {
        when (event) {
            ReviewContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(
                        ReviewContractChannel.NavigateBack
                    )
                }
            }

        }
    }

    private fun MessageContent.sendMessage() {
        viewModelScope.launch {
            this@sendMessage.let {
                _channel.send(ReviewContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
class ReviewContractState {
    private val _items = mutableStateListOf<ReviewItemUI>()
    val items: List<ReviewItemUI> = _items

    fun addItems(items: Array<CardUI>) {
        _items.clear()
        _items.addAll(items.map { it.toReviewItem() })
    }
}

@Immutable
sealed interface ReviewContractEvent {
    object OnBack : ReviewContractEvent
}

@Immutable
sealed interface ReviewContractChannel {
    object NavigateBack : ReviewContractChannel
    object Finished : ReviewContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ReviewContractChannel
}
