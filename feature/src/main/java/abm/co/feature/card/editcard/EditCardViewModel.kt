package abm.co.feature.card.editcard

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.RedesignServerRepository
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.OxfordTranslationResponseUI
import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class EditCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val languagesRepository: LanguagesRepository,
    private val redesignServerRepository: RedesignServerRepository
) : ViewModel() {

    private val card: CardUI? = savedStateHandle["card"]
    private var category: CategoryUI? = savedStateHandle["category"]

    private val _channel = Channel<EditCardContractChannel>()
    val channel: Flow<EditCardContractChannel> = _channel.receiveAsFlow()

    val uiState: EditCardContractState = EditCardContractState(
        category = category,
        learningText = card?.translation ?: "",
        nativeText = card?.name ?: ""
    )

    init {
        getCard()
    }

    private fun getCard() {
        viewModelScope.launch {
            with(uiState) {
                definitionContainer.setDefinitions(
                    listOf(
                        "the way in which people or things are placed or arranged in relation to each other",
                        "he state of being carefully and neatly arranged",
                        "\u200B  [uncountable] the state that exists when people obey laws, rules or authority"
                    )
                )
                relatedWordsContainer.setRelatedWords(
                    listOf(
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "book",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Antonym
                        ),
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "command1",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Antonym
                        ),
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "command2",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Synonym
                        ),
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "command3",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Antonym
                        ),
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "command4",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Synonym
                        ),
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "command5",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Antonym
                        ),
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI(
                            "command5",
                            EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Antonym
                        ),
                    )
                )
                translationVariantsContainer.setTranslateVariants(
                    listOf(
                        EditCardContractState.TranslationVariantsContainer.TranslationVariantUI(
                            text = "kajfjksadnfaksdjnf sd",
                            kind = CardKindUI.values()[Random.nextInt(CardKindUI.values().size)]
                        ),
                        EditCardContractState.TranslationVariantsContainer.TranslationVariantUI(
                            text = "v xmcnbolsjkgbklm kfdg",
                            kind = CardKindUI.values()[Random.nextInt(CardKindUI.values().size)]
                        ),
                        EditCardContractState.TranslationVariantsContainer.TranslationVariantUI(
                            text = "¬kafm osdfjo k",
                            kind = CardKindUI.values()[Random.nextInt(CardKindUI.values().size)]
                        ),
                        EditCardContractState.TranslationVariantsContainer.TranslationVariantUI(
                            text = "ksdfj sofjdks ho",
                            kind = CardKindUI.values()[Random.nextInt(CardKindUI.values().size)]
                        )
                    )
                )
                exampleContainer.setExamples(
                    listOf(
                        EditCardContractState.ExampleContainer.ExampleUI(
                            text = "kdfj adsjlgnja lknkfd",
                            translation = "aksdj glknsgad gmf"
                        ),
                        EditCardContractState.ExampleContainer.ExampleUI(
                            text = "ksdlgnj jdkfbndg snd f",
                            translation = "kpsdmf jnfkd m"
                        ),
                        EditCardContractState.ExampleContainer.ExampleUI(
                            text = "klsdasj gnvnxcvmns",
                            translation = ",cmvmxcnbxm,cnkdljo"
                        )
                    )
                )
            }
        }
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
                }
            }

            EditCardContractEvent.OnClickSaveCard -> {
                if (card == null) {
                } else {
                }
            }

            is EditCardContractEvent.OnEnterImage -> {
            }

            is EditCardContractEvent.OnEnterLearning -> {
                if (event.value.length < 200) {
                    uiState.wordInfoContainer.setLearningText(event.value)
                }
            }

            is EditCardContractEvent.OnEnterNative -> {
                if (event.value.length < 200) {
                    uiState.wordInfoContainer.setNativeText(event.value)
                }
            }

            is EditCardContractEvent.OnClickTranslate -> {

            }
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

@Stable
class EditCardContractState(
    category: CategoryUI?,
    learningText: String = "",
    nativeText: String = "",
    initLink: String = ""
) {

    val categoryContainer: CategoryContainer = CategoryContainer(initCategory = category)
    val wordInfoContainer: WordInfoContainer = WordInfoContainer(
        initLearningText = learningText,
        initNativeText = nativeText
    )
    val imageContainer: ImageContainer = ImageContainer(initLink = initLink)
    val translationVariantsContainer: TranslationVariantsContainer = TranslationVariantsContainer()
    val exampleContainer: ExampleContainer = ExampleContainer()
    val relatedWordsContainer: RelatedWordsContainer = RelatedWordsContainer()
    val definitionContainer: DefinitionContainer = DefinitionContainer()

    @Stable
    class TranslationVariantsContainer {
        @Stable
        data class TranslationVariantUI(
            val text: String,
            val kind: CardKindUI
        ) {
            private val _isSelected = mutableStateOf(false)
            val isSelected: State<Boolean> = _isSelected

            fun setSelected(selected: Boolean) {
                _isSelected.value = selected
            }
        }

        private val _translateVariants: SnapshotStateList<TranslationVariantUI> =
            mutableStateListOf()
        val translateVariants: List<TranslationVariantUI> = _translateVariants

        val isVisible by derivedStateOf {
            _translateVariants.isNotEmpty()
        }

        fun setTranslateVariants(translationVariants: List<TranslationVariantUI>) {
            _translateVariants.clear()
            _translateVariants.addAll(translationVariants)
        }
    }

    @Stable
    class ExampleContainer {
        @Stable
        data class ExampleUI(
            val text: String,
            val translation: String
        ) {
            fun speak(context: Context) {
                // todo
            }
        }

        private val _examples: SnapshotStateList<ExampleUI> = mutableStateListOf()
        val examples: List<ExampleUI> = _examples

        val isVisible by derivedStateOf {
            _examples.isNotEmpty()
        }

        fun setExamples(examples: List<ExampleUI>) {
            _examples.clear()
            _examples.addAll(examples)
        }
    }

    @Stable
    class RelatedWordsContainer {

        @Stable
        data class RelatedWordUI(
            val text: String,
            val kind: KindUI
        ) {
            enum class KindUI {
                Synonym,
                Antonym;
            }
        }

        private val _relatedWordsState: SnapshotStateList<RelatedWordUI> = mutableStateListOf()
        val relatedWords: List<RelatedWordUI> = _relatedWordsState

        val isVisible by derivedStateOf {
            _relatedWordsState.isNotEmpty()
        }

        fun setRelatedWords(words: List<RelatedWordUI>) {
            _relatedWordsState.clear()
            _relatedWordsState.addAll(words)
        }
    }

    @Stable
    class DefinitionContainer {

        private val _definitionState: SnapshotStateList<String> = mutableStateListOf()
        val definitions: List<String> = _definitionState

        val isVisible by derivedStateOf {
            _definitionState.isNotEmpty()
        }

        fun setDefinitions(definitions: List<String>) {
            _definitionState.clear()
            _definitionState.addAll(definitions)
        }
    }

    @Stable
    class CategoryContainer(
        initCategory: CategoryUI?
    ) {
        private val _category = mutableStateOf(initCategory)
        val category by _category
    }

    @Stable
    class WordInfoContainer(
        initLearningText: String,
        initNativeText: String
    ) {
        private val _learningLanguageText = mutableStateOf(initLearningText)
        val learningLanguageText: State<String> = _learningLanguageText

        private val _nativeLanguageText = mutableStateOf(initNativeText)
        val nativeLanguageText: State<String> = _nativeLanguageText

        fun setLearningText(text: String) {
            _learningLanguageText.value = text
        }

        fun setNativeText(text: String) {
            _nativeLanguageText.value = text
        }

    }

    @Stable
    class ImageContainer(
        initLink: String
    ) {
        private val _linkState = mutableStateOf(initLink)
        val linkState: State<String> = _linkState

        fun setLink(value: String) {
            _linkState.value = value
        }
    }
}

@Immutable
sealed interface EditCardContractEvent {

    object OnClickCategory : EditCardContractEvent

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