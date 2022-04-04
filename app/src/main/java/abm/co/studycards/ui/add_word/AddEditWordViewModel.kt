package abm.co.studycards.ui.add_word

import abm.co.studycards.R
import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.data.model.vocabulary.*
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.DictionaryRepository
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.OXFORD_CAN_TRANSLATE_MAP
import abm.co.studycards.util.base.BaseViewModel
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    private val repository: DictionaryRepository,
    @Named(Constants.USERS_REF) var userRef: DatabaseReference,
    @Named(Constants.API_KEYS) var apiKeys: DatabaseReference,
    private val firebaseRepository: ServerCloudRepository,
    prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    var translateCounts: Long = 1

    val word = savedStateHandle.get<Word>("word")
    private var categoryName = savedStateHandle.get<String>("categoryName") ?: ""
    var currentCategoryId = savedStateHandle.get<String>("categoryId")

    val sourceLanguage = prefs.getSourceLanguage()
    val targetLanguage = prefs.getTargetLanguage()

    private var translatedOxfordWord: String = "o.abylai01"
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

    private val _imageStateFlow = MutableStateFlow(word?.imageUrl)
    val imageStateFlow = _imageStateFlow.asStateFlow()

    private var oxfordApiKey = "IdIk4ortU"
    private var oxfordApiId = "IdIk4ortU"
    private var yandexApiKey = "IdIk4ortU"

    init {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("canTranslateTimeEveryDay").value?.let {
                    translateCounts = it as Long
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("ABO_ADD_WORD", error.message)
            }
        })
        apiKeys.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("oxford_key").value?.let {
                    oxfordApiKey = it.toString()
                }
                snapshot.child("oxford_id").value?.let {
                    oxfordApiId = it.toString()
                }
                snapshot.child("yandex_key").value?.let {
                    yandexApiKey = it.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }

    fun fetchWord(fromSource: Boolean) {
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

    private fun fetchYandexWord(fromSource: Boolean) {
        viewModelScope.launch(dispatcher) {
            translatedYandexWord =
                (if (fromSource) sourceWordStateFlow.value else targetWordStateFlow.value).trim()
            val sl = if (fromSource) sourceLanguage else targetLanguage
            val tl = if (fromSource) targetLanguage else sourceLanguage
            when (val wrapper = repository.getYandexWord(translatedYandexWord, sl, tl, yandexApiKey)) {
                is ResultWrapper.Error -> {
                    makeToast(wrapper.error ?: "")
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

            when (val wrapper = repository.getOxfordWord(translatedOxfordWord.trim(), sl, tl, oxfordApiId, oxfordApiKey)) {
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
        if (fromSource) {
            _sourceTranslatingStateFlow.value = false
        } else {
            _targetTranslatingStateFlow.value = false
        }
    }

    private fun changeTranslateCount() {
        userRef.updateChildren(mapOf("canTranslateTimeEveryDay" to translateCounts - 1))
    }

    private fun isSourceWordTranslatedOxford(): Boolean {
        return sourceWordStateFlow.value.lowercase().trim() == translatedOxfordWord.lowercase()
            .trim()
    }

    private fun isTargetWordTranslatedOxford(): Boolean {
        return targetWordStateFlow.value.lowercase().trim() == translatedOxfordWord.lowercase()
            .trim()
    }


    fun changeCategory(category: Category?) {
        if (category != null) {
            this.currentCategoryId = category.id
            this.categoryName = category.mainName
            _categoryStateFlow.value = categoryName
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

    fun setImage(v: String?) {
        _imageStateFlow.value = v
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
        examplesStateFlow.value = examples ?: ""
        if (!fromTarget) {
            sourceWordStateFlow.value = translations ?: ""
        } else {
            targetWordStateFlow.value = translations ?: ""
        }
    }
}

sealed class AddEditWordEventChannel {
    data class NavigateToDictionary(
        val translatedText: String?,
        val resultsEntry: ResultsEntry?,
        val fromSource: Boolean
    ) : AddEditWordEventChannel()

    object ShakeCategory:AddEditWordEventChannel()

    object PopBackStack : AddEditWordEventChannel()
}