package abm.co.studycards.ui.add_word

import abm.co.studycards.R
import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.data.model.vocabulary.*
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.DictionaryRepository
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CAN_TRANSLATE_TIME_EVERY_DAY
import abm.co.studycards.util.Constants.OXFORD_CAN_TRANSLATE_MAP
import abm.co.studycards.util.Constants.OXFORD_ID
import abm.co.studycards.util.Constants.OXFORD_KEY
import abm.co.studycards.util.Constants.YANDEX_KEY
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    private val repository: DictionaryRepository,
    private val firebaseRepository: ServerCloudRepository,
    prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    var translateCounts: Long = -1

    private val apiKeys = firebaseRepository.getApiReference()
    private val userRef = firebaseRepository.getUserReference()

    val word = savedStateHandle.get<Word>("word")
    private var categoryName = savedStateHandle.get<String>("categoryName") ?: ""
    var currentCategoryId = savedStateHandle.get<String>("categoryId")

    val sourceLanguage = prefs.getSourceLanguage()
    val targetLanguage = prefs.getTargetLanguage()

    private var translatedOxfordWord: String = "ilm"
    private var translatedYandexWord: String = ""
    private var translatedOxfordClass: ResultsEntry? = null

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

    private var oxfordApiKey = "why_do_you_need_this_b_?"
    private var oxfordApiId = "why_do_you_need_this_a_?"
    private var yandexApiKey = "why_do_you_need_this_c_?"

    var backPressedTime: Long = 0

    init {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child(CAN_TRANSLATE_TIME_EVERY_DAY).value?.let {
                    translateCounts = it as Long
                }
            }

            override fun onCancelled(error: DatabaseError) {
                translateCounts = -1
            }
        })
        apiKeys.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(dispatcher) {
                    snapshot.child(OXFORD_KEY).value?.let {
                        oxfordApiKey = it.toString()
                    }
                    snapshot.child(OXFORD_ID).value?.let {
                        oxfordApiId = it.toString()
                    }
                    snapshot.child(YANDEX_KEY).value?.let {
                        yandexApiKey = it.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(firebaseError(error.code))
            }
        })
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
                repository.getYandexWord(translatedYandexWord, sl, tl, yandexApiKey)) {
                is ResultWrapper.Error -> {
                    makeToast(wrapper.errorRes ?: "")
                    stopLoadingIcon(fromSource)
                }
                is ResultWrapper.Success -> {
                    changeTranslateCount()
                    val translated = wrapper.value.text?.joinToString(", ")
                    stopLoadingIcon(fromSource)
                    if (fromSource) {
                        targetWordStateFlow.value = translated ?: ""
                    } else {
                        sourceWordStateFlow.value = translated ?: ""
                    }
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

            when (val wrapper = repository.getOxfordWord(
                translatedOxfordWord.trim(),
                sl,
                tl,
                oxfordApiId,
                oxfordApiKey
            )) {
                is ResultWrapper.Error -> {
                    fetchYandexWord(fromSource)
                }
                is ResultWrapper.Success -> {
                    stopLoadingIcon(fromSource)
                    changeTranslateCount()
                    translatedOxfordClass = wrapper.value.results[0]
                    _eventChannel.emit(
                        AddEditWordEventChannel.NavigateToDictionary(
                            null,
                            translatedOxfordClass!!,
                            fromSource
                        )
                    )
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
            firebaseRepository.updateUserTranslateCount(translateCounts - 1)
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
                categoryName = category.mainName
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
                )
                firebaseRepository.addWord(word)
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
                firebaseRepository.deleteWord(word.categoryID, word.wordId)
                firebaseRepository.addWord(uWord)
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
        val resultsEntry: ResultsEntry?,
        val fromSource: Boolean
    ) : AddEditWordEventChannel()

    object ShakeCategory : AddEditWordEventChannel()

    object PopBackStack : AddEditWordEventChannel()
    data class ChangeImageVisibility(val enabled: Boolean) : AddEditWordEventChannel()
}