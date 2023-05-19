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

    private val isRepeat = savedStateHandle["is_repeat"] ?: false

    private val _channel = Channel<ReviewContractChannel>()
    val channel = _channel.receiveAsFlow()

    val state = ReviewContractState(isRepeat = isRepeat)

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

            ReviewContractEvent.OnFinish -> {
                viewModelScope.launch {
                    _channel.send(
                        ReviewContractChannel.Finished
                    )
                }
            }
        }
    }
}

@Stable
class ReviewContractState(val isRepeat: Boolean) {
    private val _items = mutableStateListOf<ReviewItemUI>()
    val items: List<ReviewItemUI> = _items

    fun addItems(items: Array<CardUI>) {
        _items.clear()
        _items.addAll(
            items.map {
                it.toReviewItem()
            } + ReviewItemUI("", "", "", "")
        )
    }
}

@Immutable
sealed interface ReviewContractEvent {
    object OnBack : ReviewContractEvent
    object OnFinish : ReviewContractEvent
}

@Immutable
sealed interface ReviewContractChannel {
    object NavigateBack : ReviewContractChannel
    object Finished : ReviewContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ReviewContractChannel
}
