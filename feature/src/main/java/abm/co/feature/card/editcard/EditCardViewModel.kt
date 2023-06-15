package abm.co.feature.card.editcard

import abm.co.data.model.oxford.EMPTY_TRANSLATION
import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onLeft
import abm.co.domain.base.onRight
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.domain.usecase.GetWordInfoUseCase
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.OxfordTranslationResponseUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.card.model.toUI
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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val languagesRepository: LanguagesRepository,
    private val serverRepository: ServerRepository,
    private val getWordInfoUseCase: GetWordInfoUseCase
) : ViewModel() {

    private val card: CardUI? = savedStateHandle["card"]
    private var category: CategoryUI? = savedStateHandle["category"]
    private val showProgress: Boolean = savedStateHandle["show_progress"] ?: false

    private val _channel = Channel<EditCardContractChannel>()
    val channel: Flow<EditCardContractChannel> = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(
        EditCardContractState(
            progress = if (showProgress) 0.6f else null,
            categoryName = category?.title,
            nativeText = card?.name ?: "",
            learningText = card?.translation ?: "",
            example = card?.example ?: "",
            imageURL = card?.imageUrl ?: ""
        )
    )
    val state: StateFlow<EditCardContractState> = _state.asStateFlow()

    private var oxfordResponse: OxfordTranslationResponseUI? = null
    private var checkedOxfordItemsID: List<String>? = null

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
                if (event.value.length < 500) {
                    _state.update {
                        it.copy(example = event.value)
                    }
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
                if (event.value.length < 200) {
                    _state.update {
                        it.copy(learningText = event.value)
                    }
                }
            }

            is EditCardContractEvent.OnEnterNative -> {
                if (event.value.length < 200) {
                    _state.update {
                        it.copy(nativeText = event.value)
                    }
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
                        EditCardContractChannel.NavigateToSearchHistory
                    )
                }
            }

            is EditCardContractEvent.OnClickTranslate -> {
                viewModelScope.launch {
                    val word: String = if (event.fromNative) {
                        setNativeTranslateButtonState(ButtonState.Loading)
                        state.value.nativeText.trim()
                    } else {
                        setLearningTranslateButtonState(ButtonState.Loading)
                        state.value.learningText.trim()
                    }
                    alreadyTranslatedOxfordWord(word = word)?.let {
                        _channel.send(
                            EditCardContractChannel.NavigateToWordInfo(
                                fromNativeToLearning = event.fromNative,
                                oxfordResponse = it,
                                checkedOxfordItemsID = checkedOxfordItemsID
                            )
                        )
                    } ?: getWordInfo(
                        word = word,
                        fromNative = event.fromNative
                    )
                    if (event.fromNative) {
                        setNativeTranslateButtonState(ButtonState.Normal)
                    } else {
                        setLearningTranslateButtonState(ButtonState.Normal)
                    }
                }
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
                example = this@onSaveCard.example ?: "",
                categoryID = category?.id ?: "no_category_id",
                nextRepeatTime = Calendar.getInstance().timeInMillis,
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

    private fun EditCardContractState.onUpdateCard(existedCard: CardUI) {
        viewModelScope.launch {
            val card = CardUI(
                name = nativeText,
                kind = CardKindUI.UNDEFINED,
                translation = learningText,
                imageUrl = imageURL,
                repeatedCount = 0,
                example = example ?: "",
                categoryID = category?.id ?: "no_category_id",
                nextRepeatTime = Calendar.getInstance().timeInMillis,
                cardID = existedCard.cardID,
                learnedPercent = 0f
            )
            serverRepository.updateUserCard(card.toDomain())
                .onFailure {
                    it.sendException()
                }.onSuccess {
                    if (category?.id != existedCard.categoryID) {
                        serverRepository.removeUserCard(
                            categoryID = existedCard.categoryID,
                            cardID = existedCard.cardID
                        )
                    }
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

    fun onSelectedCategory(category: CategoryUI) {
        this.category = category
        _state.update {
            it.copy(categoryName = category.title)
        }
    }

    private fun setNativeTranslateButtonState(state: ButtonState) {
        _state.update {
            it.copy(nativeTranslateButtonState = state)
        }
    }

    private fun setLearningTranslateButtonState(state: ButtonState) {
        _state.update {
            it.copy(learningTranslateButtonState = state)
        }
    }

    fun setOxfordResponse(
        checkedOxfordItemsID: Array<String>,
        fromNative: Boolean
    ) {
        this.checkedOxfordItemsID = checkedOxfordItemsID.toList()
        updateStateAfterOxfordResponse(
            fromNative = fromNative,
            checkedOxfordItemsID = checkedOxfordItemsID
        )
    }

    private fun updateStateAfterOxfordResponse(
        oxfordResponse: OxfordTranslationResponseUI? = this.oxfordResponse,
        checkedOxfordItemsID: Array<String>,
        fromNative: Boolean
    ) {
        _state.update { oldState ->
            val entries = oxfordResponse?.lexicalEntry?.flatMap { lexicalEntryUI ->
                lexicalEntryUI.entries?.filter { checkedOxfordItemsID.contains(it.id) }
                    ?: emptyList()
            }
            val translations = entries?.filter {
                it.translation != EMPTY_TRANSLATION
            }?.joinToString("\n") { entryUI ->
                entryUI.translation
            } ?: ""
            val native = if (fromNative) {
                oldState.nativeText
            } else {
                translations
            }
            val learning = if (fromNative) {
                translations
            } else {
                oldState.learningText
            }
            val examples = entries?.flatMap { entryUI ->
                entryUI.examples ?: emptyList()
            }?.mapIndexed { index, exampleUI ->
                (index + 1) to exampleUI
            }?.joinToString("") { "${it.first}. ${it.second.text} - ${it.second.translation}\n" }
            oldState.copy(
                nativeText = native,
                learningText = learning,
                example = examples
            )
        }
    }

    private suspend fun getWordInfo(
        word: String,
        fromNative: Boolean
    ) {
        getWordInfoUseCase(word = word, fromNative = fromNative)
            .onFailure {
                it.sendException()
            }
            .onSuccess { either ->
                either.onLeft { response ->
                    oxfordResponse = response.toUI().also {
                        _channel.send(
                            EditCardContractChannel.NavigateToWordInfo(
                                fromNativeToLearning = fromNative,
                                oxfordResponse = it,
                                checkedOxfordItemsID = null
                            )
                        )
                    }
                }.onRight { response ->
                    val native = if (fromNative) {
                        word
                    } else {
                        response.text?.joinToString() ?: ""
                    }
                    val learning = if (fromNative) {
                        response.text?.joinToString() ?: ""
                    } else {
                        word
                    }
                    _state.update { oldState ->
                        oldState.copy(
                            nativeText = native,
                            learningText = learning
                        )
                    }
                }
            }
    }

    private fun alreadyTranslatedOxfordWord(
        word: String,
        oxfordResponse: OxfordTranslationResponseUI? = this.oxfordResponse
    ): OxfordTranslationResponseUI? {
        return if (oxfordResponse?.word?.lowercase()?.trim() == word.lowercase().trim()) {
            oxfordResponse
        } else {
            null
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent().let {
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
    val nativeTranslateButtonState: ButtonState = ButtonState.Normal,
    val learningTranslateButtonState: ButtonState = ButtonState.Normal,
    val learningText: String = "",
    val nativeText: String = "",
    val example: String? = null,
    val imageURL: String = ""
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

    data class NavigateToWordInfo(
        val fromNativeToLearning: Boolean,
        val oxfordResponse: OxfordTranslationResponseUI,
        val checkedOxfordItemsID: List<String>?
    ) : EditCardContractChannel

    object NavigateBack : EditCardContractChannel

    object NavigateToSearchHistory : EditCardContractChannel
}