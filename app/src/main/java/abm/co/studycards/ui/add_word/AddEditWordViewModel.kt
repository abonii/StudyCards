package abm.co.studycards.ui.add_word

import abm.co.studycards.R
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.*
import abm.co.studycards.domain.usecases.*
import abm.co.studycards.util.Constants.OXFORD_CAN_TRANSLATE_MAP
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    private val getYandexTranslatedWordUseCase: GetYandexTranslatedWordUseCase,
    private val getOxfordTranslatedResultUseCase: GetOxfordTranslatedResultUseCase,
    private val addUserWordUseCase: AddUserWordUseCase,
    private val updateUserWordUseCase: UpdateUserWordUseCase,
    private val updateUserTranslateCountUseCase: UpdateUserTranslateCountUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private var translateCounts: Long = -1

    val word = savedStateHandle.get<Word>("word")
    private var categoryName = savedStateHandle.get<String>("categoryName") ?: ""
    var currentCategoryId = savedStateHandle.get<String>("categoryId")

    val sourceLanguage = prefs.getSourceLanguage()
    val targetLanguage = prefs.getTargetLanguage()

    private var translatedOxfordWord: String = "ilm"
    private var translatedYandexWord: String = ""
    private var translatedOxfordClass: OxfordResult? = null

    private val _eventChannel =
        MutableSharedFlow<AddEditWordEventChannel>()
    val eventChannel = _eventChannel.asSharedFlow()

    val sourceWordStateFlow = MutableStateFlow(word?.name ?: "")

    val targetWordStateFlow = MutableStateFlow(word.translationsToString())

    val examplesStateFlow = MutableStateFlow(word.examplesToString())

    private val _categoryStateFlow = MutableStateFlow(categoryName)
    val categoryStateFlow = _categoryStateFlow.asStateFlow()

    private val _sourceTranslatingStateFlow = MutableStateFlow(false)
    val sourceTranslatingStateFlow = _sourceTranslatingStateFlow.asStateFlow()

    private val _targetTranslatingStateFlow = MutableStateFlow(false)
    val targetTranslatingStateFlow = _targetTranslatingStateFlow.asStateFlow()

    val imageStateFlow = MutableStateFlow(word?.imageUrl)
    val imageCanSetUrlStateFlow = MutableStateFlow(true)
    val imageVisibleStateFlow = MutableStateFlow(word?.imageUrl?.isNotBlank() == true)

    private lateinit var config: Config

    var backPressedTime: Long = 0

    init {
        viewModelScope.launch(dispatcher) {
            getUserInfoUseCase().collectLatest {
                translateCounts = it.translateCounts
            }
        }
        viewModelScope.launch {
            config = getConfigUseCase()
        }
    }

    fun fetchWord(fromSource: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (translateCounts > 0) {
                if (fromSource) _sourceTranslatingStateFlow.value = true
                else _targetTranslatingStateFlow.value = true
                if (checkIfOxfordSupport(fromSource) && isItMoreThanOneWord(fromSource)) {
                    fetchOxfordWord(fromSource)
                } else fetchYandexWord(fromSource)
            } else {
                makeToast(R.string.translate_count_spend)
            }
        }
    }

    private fun fetchYandexWord(fromSource: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (((fromSource && isSourceWordTranslatedYandex()) ||
                        (!fromSource && isTargetWordTranslatedYandex()))
            ) {
                if (fromSource) {
                    targetWordStateFlow.value = translatedYandexWord
                } else {
                    sourceWordStateFlow.value = translatedYandexWord
                }
                _sourceTranslatingStateFlow.value = false
                _targetTranslatingStateFlow.value = false
                return@launch
            }
            translatedYandexWord =
                (if (fromSource) sourceWordStateFlow.value else targetWordStateFlow.value).trim()
            val sl = if (fromSource) sourceLanguage else targetLanguage
            val tl = if (fromSource) targetLanguage else sourceLanguage
            when (val wrapper =
                getYandexTranslatedWordUseCase(translatedYandexWord, sl, tl, config.yandexKey)) {
                is ResultWrapper.Error -> {
                    makeToast(wrapper.res)
                    stopLoadingIcon(fromSource)
                }
                is ResultWrapper.Success -> {
                    changeTranslateCount()
                    val translated = wrapper.value
                    stopLoadingIcon(fromSource)
                    if (fromSource) {
                        targetWordStateFlow.value = translated
                    } else {
                        sourceWordStateFlow.value = translated
                    }
                }
                ResultWrapper.Loading -> {

                }
            }
        }
    }

    private fun fetchOxfordWord(fromSource: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (((fromSource && isSourceWordTranslatedOxford()) ||
                        (!fromSource && isTargetWordTranslatedOxford())) && translatedOxfordClass != null
            ) {
                _eventChannel.emit(
                    AddEditWordEventChannel.NavigateToDictionary(
                        null,
                        translatedOxfordClass!!,
                        fromSource
                    )
                )
                _sourceTranslatingStateFlow.value = false
                _targetTranslatingStateFlow.value = false
                return@launch
            }
            val sl = if (fromSource) sourceLanguage else targetLanguage
            val tl = if (fromSource) targetLanguage else sourceLanguage

            translatedOxfordWord =
                if (fromSource) sourceWordStateFlow.value else targetWordStateFlow.value

            when (val wrapper = getOxfordTranslatedResultUseCase(
                translatedOxfordWord.trim(),
                sl, tl, config.oxfordId, config.oxfordKey
            )) {
                is ResultWrapper.Error -> {
                    fetchYandexWord(fromSource)
                }
                is ResultWrapper.Success -> {
                    stopLoadingIcon(fromSource)
                    changeTranslateCount()
                    translatedOxfordClass = wrapper.value
                    _eventChannel.emit(
                        AddEditWordEventChannel.NavigateToDictionary(
                            null,
                            translatedOxfordClass!!,
                            fromSource
                        )
                    )
                }
                ResultWrapper.Loading -> {

                }
            }
        }
    }

    private fun stopLoadingIcon(fromSource: Boolean) {
        viewModelScope.launch {
            if (fromSource) {
                _sourceTranslatingStateFlow.value = false
            } else {
                _targetTranslatingStateFlow.value = false
            }
        }
    }

    private fun changeTranslateCount() {
        viewModelScope.launch(dispatcher) {
            updateUserTranslateCountUseCase(translateCounts - 1)
        }
    }

    private fun isSourceWordTranslatedOxford(): Boolean {
        return sourceWordStateFlow.value.lowercase().trim() == translatedOxfordWord.lowercase()
            .trim()
    }

    private fun isTargetWordTranslatedOxford(): Boolean {
        return targetWordStateFlow.value.lowercase().trim() == translatedOxfordWord.lowercase()
            .trim()
    }

    private fun isSourceWordTranslatedYandex(): Boolean {
        return sourceWordStateFlow.value.lowercase().trim() == translatedYandexWord.lowercase()
            .trim()
    }

    private fun isTargetWordTranslatedYandex(): Boolean {
        return targetWordStateFlow.value.lowercase().trim() == translatedYandexWord.lowercase()
            .trim()
    }


    fun changeCategory(category: Category?) {
        viewModelScope.launch(dispatcher) {
            if (category != null) {
                currentCategoryId = category.id
                categoryName = category.name
                _categoryStateFlow.value = categoryName
            }
        }
    }

    private fun isItMoreThanOneWord(fromSource: Boolean): Boolean {
        return ((fromSource && sourceWordStateFlow.value.trim().split(" ", ",", ".").size <= 1)
                || (!fromSource && targetWordStateFlow.value.trim().split(" ", ",", ".").size <= 1))
    }

    private fun checkIfOxfordSupport(isFromSource: Boolean): Boolean {
        return if (isFromSource) {
            OXFORD_CAN_TRANSLATE_MAP[sourceLanguage]?.contains(targetLanguage) ?: false
        } else {
            OXFORD_CAN_TRANSLATE_MAP[targetLanguage]?.contains(sourceLanguage) ?: false
        }
    }

    fun saveWord() {
        viewModelScope.launch(dispatcher) {
            if (sourceWordStateFlow.value.isBlank() || targetWordStateFlow.value.isBlank()) {
                makeToast(R.string.name_translation_not_empty)
                return@launch
            }
            if (currentCategoryId == null) {
                makeToast(R.string.choose_category)
                _eventChannel.emit(AddEditWordEventChannel.ShakeCategory)
                return@launch
            }
            if (word == null) {
                val word = Word(
                    name = sourceWordStateFlow.value,
                    translations = targetWordStateFlow.value.translationsToList(),
                    imageUrl = imageStateFlow.value ?: "",
                    examples = examplesStateFlow.value.examplesToList(),
                    learnOrKnown = LearnOrKnown.UNDEFINED.getType(),
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    categoryID = currentCategoryId ?: "default",
                    nextRepeatTime = 0,
                    repeatCount = 0,
                    wordId = "default"
                )
                addUserWordUseCase(word)
            } else {
                val uWord = word.copy(
                    name = sourceWordStateFlow.value,
                    translations = targetWordStateFlow.value.translationsToList(),
                    examples = examplesStateFlow.value.examplesToList(),
                    categoryID = currentCategoryId ?: "default",
                    learnOrKnown = LearnOrKnown.UNDEFINED.getType(),
                    imageUrl = imageStateFlow.value ?: "",
                    repeatCount = 0
                )
                updateUserWordUseCase(word, uWord)
            }
            _eventChannel.emit(AddEditWordEventChannel.PopBackStack)
        }
    }

    fun onDictionaryReceived(examples: String?, translations: String?, fromTarget: Boolean) {
        viewModelScope.launch(dispatcher) {
            examplesStateFlow.value = examples ?: ""
            if (!fromTarget) {
                sourceWordStateFlow.value = translations ?: ""
            } else {
                targetWordStateFlow.value = translations ?: ""
            }
        }
    }

    fun changeEditableImageUrl(hasFocus: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (!hasFocus) {
                _eventChannel.emit(
                    AddEditWordEventChannel.ChangeImageVisibility(
                        imageStateFlow.value?.isNotBlank() == true
                    )
                )
            }
            imageCanSetUrlStateFlow.value = !hasFocus
        }
    }
}

sealed class AddEditWordEventChannel {
    data class NavigateToDictionary(
        val translatedText: String?,
        val resultsEntry: OxfordResult?,
        val fromSource: Boolean
    ) : AddEditWordEventChannel()

    object ShakeCategory : AddEditWordEventChannel()

    object PopBackStack : AddEditWordEventChannel()
    data class ChangeImageVisibility(val enabled: Boolean) : AddEditWordEventChannel()
}