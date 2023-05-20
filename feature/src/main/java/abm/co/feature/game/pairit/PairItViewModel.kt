package abm.co.feature.game.pairit

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.pairit.model.PairItemUI
import abm.co.feature.game.pairit.model.toPairLearningItem
import abm.co.feature.game.pairit.model.toPairNativeItem
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val SCALE_DURATION = 500

@HiltViewModel
class PairItViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cards: Array<CardUI> = savedStateHandle["cards"]
        ?: throw RuntimeException("cannot be empty CARDS argument")

    private val isRepeat = savedStateHandle["is_repeat"] ?: false

    private val _channel = Channel<PairItContractChannel>()
    val channel = _channel.receiveAsFlow()

    val uiState: PairItContractState = PairItContractState(isRepeat = isRepeat)

    private val _dialogState = MutableStateFlow(PairItContractState.Dialog())
    val dialogState = _dialogState.asStateFlow()

    val progress = snapshotFlow {
        try {
            uiState.correctNativeItemsID.size.toFloat() / cards.size
        } catch (e: Exception) {
            0f
        }
    }

    init {
        setupCards()
    }

    fun onEvent(event: PairItContractEvent) {
        when (event) {
            PairItContractEvent.OnBack -> {
                _dialogState.update { oldState ->
                    oldState.copy(
                        backPressConfirm = true
                    )
                }
            }

            is PairItContractEvent.OnClickLearningCard -> {
                uiState.selectedLearningItemID.value = event.value.cardID
            }

            is PairItContractEvent.OnClickNativeCard -> {
                uiState.selectedNativeItemID.value = event.value.cardID
            }

            is PairItContractEvent.OnLearningCorrectAnimationFinished -> {
                uiState.correctLearningItemsID.remove(event.value.cardID)
            }

            is PairItContractEvent.OnLearningIncorrectAnimationFinished -> {
                uiState.incorrectLearningItemID.value = null
            }

            is PairItContractEvent.OnNativeIncorrectAnimationFinished -> {
                uiState.incorrectNativeItemID.value = null
            }

            PairItContractEvent.OnDismissDialog -> {
                _dialogState.update {
                    PairItContractState.Dialog()
                }
            }
        }
    }

    private fun setupCards() {
        viewModelScope.launch {
            uiState.nativeItems.addAll(cards.map { it.toPairNativeItem() }.shuffled())
            uiState.learningItems.addAll(cards.map { it.toPairLearningItem() }.shuffled())
            listenToLists()
        }
    }

    private fun listenToLists() {
        snapshotFlow {
            uiState.correctNativeItemsID.size == uiState.nativeItems.size ||
                    uiState.correctLearningItemsID.size == uiState.learningItems.size
        }.filter {
            it
        }.onEach {
            delay(SCALE_DURATION.toDuration(DurationUnit.MILLISECONDS)) // delay of animation
            _channel.send(PairItContractChannel.Finished)
        }.launchIn(viewModelScope)

        snapshotFlow {
            uiState.selectedNativeItemID.value to uiState.selectedLearningItemID.value
        }.filter { (native, learning) ->
            native != null && learning != null
        }.onEach { (native, learning) ->
            if (native == learning) {
                native?.let { uiState.correctNativeItemsID.add(it) }
                learning?.let { uiState.correctLearningItemsID.add(it) }
            } else {
                learning?.let { uiState.incorrectLearningItemID.value = it }
                native?.let { uiState.incorrectNativeItemID.value = it }
            }
            uiState.selectedLearningItemID.value = null
            uiState.selectedNativeItemID.value = null
        }.launchIn(viewModelScope)
    }
}

@Stable
class PairItContractState(val isRepeat: Boolean) {

    val nativeItems = mutableStateListOf<PairItemUI>()
    val learningItems = mutableStateListOf<PairItemUI>()

    val selectedNativeItemID = mutableStateOf<String?>(null)
    val selectedLearningItemID = mutableStateOf<String?>(null)

    val correctNativeItemsID = mutableStateListOf<String>()
    val correctLearningItemsID = mutableStateListOf<String>()

    val incorrectLearningItemID = mutableStateOf<String?>(null)
    val incorrectNativeItemID = mutableStateOf<String?>(null)

    data class Dialog(
        val backPressConfirm: Boolean = false,
        val continueRepeatOrFinish: Boolean = false
    )
}

@Immutable
sealed interface PairItContractEvent {
    object OnBack : PairItContractEvent
    object OnDismissDialog : PairItContractEvent
    data class OnClickNativeCard(val value: PairItemUI) : PairItContractEvent
    data class OnClickLearningCard(val value: PairItemUI) : PairItContractEvent
    data class OnNativeIncorrectAnimationFinished(val value: PairItemUI) : PairItContractEvent
    data class OnLearningIncorrectAnimationFinished(val value: PairItemUI) : PairItContractEvent
    data class OnLearningCorrectAnimationFinished(val value: PairItemUI) : PairItContractEvent
}

@Immutable
sealed interface PairItContractChannel {
    object Finished : PairItContractChannel
    data class ShowMessage(val messageContent: MessageContent) : PairItContractChannel
}
