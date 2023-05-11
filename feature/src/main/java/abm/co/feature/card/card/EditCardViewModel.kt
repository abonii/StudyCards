package abm.co.feature.card.card

import abm.co.designsystem.R
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.model.oxford.OxfordTranslationResponse
import abm.co.domain.repository.DictionaryRepository
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.toUI
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val languagesRepository: LanguagesRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val card: CardUI? = savedStateHandle["card"]
    private val category: CategoryUI? = savedStateHandle["category"]
    private val showProgress: Boolean = savedStateHandle["show_progress"] ?: false

    private val _channel = Channel<EditCardContractChannel>()
    val channel: Flow<EditCardContractChannel> = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(
        EditCardContractState(
            progress = if (showProgress) 0.6f else null,
            categoryName = category?.name,
            nativeText = card?.name ?: "",
            learningText = card?.translation ?: "",
            example = card?.example ?: "",
            imageURL = card?.imageUrl ?: ""
        )
    )
    val state: StateFlow<EditCardContractState> = _state.asStateFlow()

    init {
        setLanguages()
    }

    fun onEvent(event: EditCardContractEvent) {
        when (event) {
            EditCardContractEvent.OnClickBack -> {
                viewModelScope.launch {
                    _channel.send(EditCardContractChannel.NavigateBack)
                }
            }

            EditCardContractEvent.OnClickCategory -> {
                viewModelScope.launch {
                    _channel.send(EditCardContractChannel.ChangeCategory(card?.categoryID))
                }
            }

            is EditCardContractEvent.OnClickEnterExample -> {
                _state.update {
                    it.copy(example = event.value)
                }
            }

            EditCardContractEvent.OnClickSaveCard -> {
                if (card == null) {
                    state.value.onSaveCard()
                } else {
                    state.value.onUpdateCard(existedCard = card)
                }
            }

            is EditCardContractEvent.OnEnterImage -> {
                _state.update {
                    it.copy(imageURL = event.value)
                }
            }

            is EditCardContractEvent.OnEnterLearning -> {
                _state.update {
                    it.copy(learningText = event.value)
                }
            }

            is EditCardContractEvent.OnEnterNative -> {
                _state.update {
                    it.copy(nativeText = event.value)
                }
            }

            EditCardContractEvent.OnFinishProgress -> {
                viewModelScope.launch {
                    _channel.send(EditCardContractChannel.NavigateBack)
                }
            }

            EditCardContractEvent.OnClickSearchHistory -> {
                viewModelScope.launch {
                    _channel.send(
                        EditCardContractChannel.ShowMessage(
                            MessageContent.Snackbar.MessageContentRes(
                                titleRes = R.string.Messages_OK,
                                subtitleRes = R.string.Messages_NotReleasedYet_title,
                                type = MessageType.Info
                            ) // todo not released
                        )
                    )
                }
            }

            is EditCardContractEvent.OnClickTranslate -> {
                // todo do request to oxford and yandex
            }
        }
    }

    private fun setLanguages() {
        viewModelScope.launch {
            val nativeLanguage = languagesRepository.getNativeLanguage().firstOrNull()?.toUI()
            val learningLanguage = languagesRepository.getLearningLanguage().firstOrNull()?.toUI()
            _state.update {
                it.copy(
                    nativeLanguage = nativeLanguage,
                    learningLanguage = learningLanguage
                )
            }
        }
    }

    private fun EditCardContractState.onSaveCard() {
        viewModelScope.launch {
            val card = CardUI(
                name = this@onSaveCard.nativeText,
                kind = CardKindUI.UNDEFINED,
                translation = this@onSaveCard.learningText,
                imageUrl = this@onSaveCard.imageURL,
                repeatedCount = 0,
                example = this@onSaveCard.example,
                categoryID = category?.id ?: "no_category_id",
                nextRepeatTime = System.currentTimeMillis(),
                cardID = "",
                learnedPercent = 0f
            )
            serverRepository.createUserCard(card.toDomain())
                .onFailure {
                    it.sendException()
                }.onSuccess {
                    if (showProgress) {
                        _state.update {
                            it.copy(progress = 1f)
                        }
                    } else {
                        _channel.send(EditCardContractChannel.NavigateBack)
                    }
                }
        }
    }

    private fun EditCardContractState.onUpdateCard(
        existedCard: CardUI
    ) {
        viewModelScope.launch {
            val card = CardUI(
                name = nativeText,
                kind = CardKindUI.UNDEFINED,
                translation = learningText,
                imageUrl = imageURL,
                repeatedCount = 0,
                example = example,
                categoryID = category?.id ?: "no_category_id",
                nextRepeatTime = System.currentTimeMillis(),
                cardID = existedCard.cardID,
                learnedPercent = 0f
            )
            serverRepository.updateUserCard(card.toDomain())
                .onFailure {
                    it.sendException()
                }.onSuccess {
                    if (showProgress) {
                        _state.update {
                            it.copy(progress = 1f)
                        }
                    } else {
                        _channel.send(EditCardContractChannel.NavigateBack)
                    }
                }
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(EditCardContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class EditCardContractState(
    val progress: Float?, // 0..1
    val categoryName: String?,
    val nativeLanguage: LanguageUI? = null,
    val learningLanguage: LanguageUI? = null,
    val learningText: String = "",
    val nativeText: String = "",
    val example: String = "",
    val imageURL: String = "",
    val oxfordTranslationResponse: OxfordTranslationResponse? = null,
)

@Immutable
sealed interface EditCardContractEvent {

    object OnFinishProgress : EditCardContractEvent

    object OnClickCategory : EditCardContractEvent

    object OnClickSearchHistory : EditCardContractEvent

    object OnClickSaveCard : EditCardContractEvent

    object OnClickBack : EditCardContractEvent

    data class OnEnterLearning(val value: String) : EditCardContractEvent

    data class OnClickTranslate(val fromNative: Boolean) : EditCardContractEvent

    data class OnEnterNative(val value: String) : EditCardContractEvent

    data class OnEnterImage(val value: String) : EditCardContractEvent

    data class OnClickEnterExample(val value: String) : EditCardContractEvent
}

@Immutable
sealed interface EditCardContractChannel {
    data class ShowMessage(val messageContent: MessageContent) : EditCardContractChannel

    data class ChangeCategory(
        val categoryId: String?
    ) : EditCardContractChannel

    object NavigateBack : EditCardContractChannel

    object NavigateToSearchHistory : EditCardContractChannel
}