package abm.co.feature.game.parit

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.parit.model.PairItemUI
import abm.co.feature.game.parit.model.toPairLearningItem
import abm.co.feature.game.parit.model.toPairNativeItem
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val SCALE_DURATION = 700

@HiltViewModel
class PairItViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cards: Array<CardUI> = savedStateHandle["cards"]
        ?: throw RuntimeException("cannot be empty CARDS argument")

    private val _channel = Channel<PairItContractChannel>()
    val channel = _channel.receiveAsFlow()

    val state: PairItContractState = PairItContractState()

    init {
        setupCards()
    }

    fun onEvent(event: PairItContractEvent) {
        when (event) {
            PairItContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(
                        PairItContractChannel.NavigateBack
                    )
                }
            }

            is PairItContractEvent.OnClickLearningCard -> {
                state.selectedLearningItemID.value = event.value.cardID
            }

            is PairItContractEvent.OnClickNativeCard -> {
                state.selectedNativeItemID.value = event.value.cardID
            }

            is PairItContractEvent.OnLearningCorrectAnimationFinished -> {
                state.correctLearningItemsID.remove(event.value.cardID)
            }

            is PairItContractEvent.OnLearningIncorrectAnimationFinished -> {
                state.incorrectLearningItemID.value = null
            }

            is PairItContractEvent.OnNativeIncorrectAnimationFinished -> {
                state.incorrectNativeItemID.value = null
            }
        }
    }

    private fun setupCards() {
        viewModelScope.launch {
            state.nativeItems.addAll(cards.map { it.toPairNativeItem() }.shuffled())
            state.learningItems.addAll(cards.map { it.toPairLearningItem() }.shuffled())
            listenToLists()
        }
    }

    private fun listenToLists() {
        snapshotFlow {
            state.correctNativeItemsID.size == state.nativeItems.size ||
                    state.correctLearningItemsID.size == state.learningItems.size
        }.filter {
            it
        }.onEach {
            delay(SCALE_DURATION.toDuration(DurationUnit.MILLISECONDS)) // delay of animation
            _channel.send(PairItContractChannel.Finished)
        }.launchIn(viewModelScope)

        snapshotFlow {
            state.selectedNativeItemID.value to state.selectedLearningItemID.value
        }.filter { (native, learning) ->
            native != null && learning != null
        }.onEach { (native, learning) ->
            if (native == learning) {
                native?.let { state.correctNativeItemsID.add(it) }
                learning?.let { state.correctLearningItemsID.add(it) }
            } else {
                learning?.let { state.incorrectLearningItemID.value = it }
                native?.let { state.incorrectNativeItemID.value = it }
            }
            state.selectedLearningItemID.value = null
            state.selectedNativeItemID.value = null
        }.launchIn(viewModelScope)
    }

    private fun MessageContent.sendMessage() {
        viewModelScope.launch {
            this@sendMessage.let {
                _channel.send(PairItContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
class PairItContractState {

    val nativeItems = mutableStateListOf<PairItemUI>()
    val learningItems = mutableStateListOf<PairItemUI>()

    val selectedNativeItemID = mutableStateOf<String?>(null)
    val selectedLearningItemID = mutableStateOf<String?>(null)

    val correctNativeItemsID = mutableStateListOf<String>()
    val correctLearningItemsID = mutableStateListOf<String>()

    val incorrectLearningItemID = mutableStateOf<String?>(null)
    val incorrectNativeItemID = mutableStateOf<String?>(null)
}

@Immutable
sealed interface PairItContractEvent {
    object OnBack : PairItContractEvent
    data class OnClickNativeCard(val value: PairItemUI) : PairItContractEvent
    data class OnClickLearningCard(val value: PairItemUI) : PairItContractEvent
    data class OnNativeIncorrectAnimationFinished(val value: PairItemUI) : PairItContractEvent
    data class OnLearningIncorrectAnimationFinished(val value: PairItemUI) : PairItContractEvent
    data class OnLearningCorrectAnimationFinished(val value: PairItemUI) : PairItContractEvent
}

@Immutable
sealed interface PairItContractChannel {
    object NavigateBack : PairItContractChannel
    object Finished : PairItContractChannel
    data class ShowMessage(val messageContent: MessageContent) : PairItContractChannel
}
