package abm.co.feature.card.wordinfo

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.feature.R
import abm.co.feature.card.model.OxfordEntryUI
import abm.co.feature.card.model.OxfordTranslationResponseUI
import abm.co.feature.utils.TextToSpeechManager
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordInfoViewModel @Inject constructor(
    private val textToSpeechManager: TextToSpeechManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val fromNativeToLearning: Boolean = savedStateHandle["from_native_to_learning"]
        ?: throw RuntimeException("cannot be empty FROM_NATIVE_TO_LEARNING argument")
    private val oxfordResponse: OxfordTranslationResponseUI = savedStateHandle["oxford_response"]
        ?: throw RuntimeException("cannot be empty OXFORD_RESPONSE argument")
    private val oxfordCheckedItemsID: Array<String> = savedStateHandle["oxford_checked_items_id"]
        ?: emptyArray()

    private val _channel = Channel<WordInfoContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state: MutableStateFlow<WordInfoContractState> =
        MutableStateFlow(WordInfoContractState(oxfordResponse = oxfordResponse))
    val state: StateFlow<WordInfoContractState> = _state.asStateFlow()

    val checkedItemsID = mutableStateListOf(*oxfordCheckedItemsID)

    fun onEvent(event: WordInfoContractEvent) {
        when (event) {
            WordInfoContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(
                        WordInfoContractChannel.NavigateBack
                    )
                }
            }

            is WordInfoContractEvent.OnClickEntry -> {
                if (checkedItemsID.contains(event.value.id)) {
                    checkedItemsID.remove(event.value.id)
                } else {
                    checkedItemsID.add(event.value.id)
                }
            }

            is WordInfoContractEvent.OnClickPlayText -> {
                speak(event.value)
            }
        }
    }

    private fun speak(value: String) {
        viewModelScope.launch {
            val canSpeak = textToSpeechManager.speakAndGet(value)
            if (!canSpeak) {
                _channel.send(
                    WordInfoContractChannel.ShowMessage(
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

    fun getCheckedItemsID(): Array<String> {
        return checkedItemsID.toTypedArray()
    }

    fun getFromNative(): Boolean {
        return fromNativeToLearning
    }

    override fun onCleared() {
        textToSpeechManager.clear()
        super.onCleared()
    }
}

@Immutable
data class WordInfoContractState(
    val oxfordResponse: OxfordTranslationResponseUI
)

@Immutable
sealed interface WordInfoContractEvent {
    object OnBack : WordInfoContractEvent
    data class OnClickEntry(val value: OxfordEntryUI) : WordInfoContractEvent
    data class OnClickPlayText(val value: String) : WordInfoContractEvent
}

@Immutable
sealed interface WordInfoContractChannel {
    object NavigateBack : WordInfoContractChannel
    data class ShowMessage(val messageContent: MessageContent) : WordInfoContractChannel
}
