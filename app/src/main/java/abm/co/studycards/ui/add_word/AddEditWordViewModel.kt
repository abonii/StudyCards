package abm.co.studycards.ui.add_word

import abm.co.studycards.R
import abm.co.studycards.data.ErrorStatus
import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.FirebaseRepository
import abm.co.studycards.data.repository.VocabularyRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    private val repository: VocabularyRepository,
    @Named(Constants.CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(Constants.USERS_REF) var userRef: DatabaseReference,
    prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    var translateCounts: Int = 10
    val word = savedStateHandle.get<Word>("word")
    var category = savedStateHandle.get<String>("categoryName") ?: ""
    var imageUrl: String? = word?.imageUrl
    var sourceWord: String = word?.name ?: ""
    var currentCategoryId: String? = word?.categoryID
    var targetWord: String = word?.translations?.joinToString(", ") ?: ""
    var exampleText: String = word?.examples?.joinToString("\n") ?: ""
    val sourceLanguage = prefs.getSourceLanguage()
    val targetLanguage = prefs.getTargetLanguage()
    private var translatedOxfordWord: String = "o.abylai01"
    private var translatedYandexWord: String = ""
    private var translatedOxfordClass: ResultsEntry? = null
    private var translatedYandexClass: String? = null
    private val firebaseRepository = FirebaseRepository(categoriesDbRef)

    private val _stateFlow = MutableStateFlow<AddEditWordUi>(AddEditWordUi.Default)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        _stateFlow.value = AddEditWordUi.CategoryChanged(category)
    }

    fun fetchWord(fromSource: Boolean) {
        if (translateCounts > 0) {
            if (checkIfOxfordSupport(fromSource) && isItMoreThanOneWord(fromSource)) {
                fetchOxfordWord(fromSource)
            } else fetchYandexWord(fromSource)
        } else {
            makeToast(R.string.translate_count_spend)
        }
    }

    private fun fetchYandexWord(fromSource: Boolean) {
        launchIO {
            _stateFlow.value = AddEditWordUi.Loading(fromSource)
            delay(50)
            translatedYandexWord = if (fromSource) sourceWord else targetWord
            val sl = if (fromSource) sourceLanguage else targetLanguage
            val tl = if (fromSource) targetLanguage else sourceLanguage
            when (val wrapper = repository.getYandexWord(translatedYandexWord.trim(), sl, tl)) {
                is ResultWrapper.Error -> {
                    _stateFlow.value = AddEditWordUi.Error(null, wrapper.error)
                }
                is ResultWrapper.Success -> {
                    changeTranslateCount()
                    translatedYandexClass = wrapper.value.text?.joinToString(", ") ?: ""
                    _stateFlow.value =
                        AddEditWordUi.SuccessYandex(translatedYandexClass!!, fromSource)
                }
            }
        }
    }

    private fun fetchOxfordWord(fromSource: Boolean) {
        launchIO {
            _stateFlow.value = AddEditWordUi.Loading(fromSource)
            delay(50)
            if (((fromSource && isSourceWordTranslatedOxford()) || (!fromSource && isTargetWordTranslatedOxford())) && translatedOxfordClass != null) {
                _stateFlow.value = AddEditWordUi.SuccessOxford(translatedOxfordClass!!, fromSource)
                return@launchIO
            }
            val sl = if (fromSource) sourceLanguage else targetLanguage
            val tl = if (fromSource) targetLanguage else sourceLanguage

            translatedOxfordWord = if (fromSource) sourceWord else targetWord

            when (val wrapper = repository.getOxfordWord(translatedOxfordWord.trim(), sl, tl)) {
                is ResultWrapper.Error -> {
                    fetchYandexWord(fromSource)
                    makeToast(wrapper.error ?: "")
                }
                is ResultWrapper.Success -> {
                    changeTranslateCount()
                    translatedOxfordClass = wrapper.value.results[0]
                    _stateFlow.value =
                        AddEditWordUi.SuccessOxford(translatedOxfordClass!!, fromSource)
                }
            }
        }
    }

    private fun changeTranslateCount() {
        userRef.setValue(mapOf("canTranslateTimeEveryDay" to translateCounts - 1))
    }

//    private fun isSourceWordTranslatedYandex(): Boolean {
//        return sourceWord.lowercase().trim() == translatedYandexWord.lowercase().trim()
//    }

    fun isSourceWordTranslatedOxford(): Boolean {
        return sourceWord.lowercase().trim() == translatedOxfordWord.lowercase().trim()
    }

//    private fun isTargetWordTranslatedYandex(): Boolean {
//        return targetWord.lowercase().trim() == translatedYandexWord.lowercase().trim()
//    }

    fun isTargetWordTranslatedOxford(): Boolean {
        return targetWord.lowercase().trim() == translatedOxfordWord.lowercase().trim()
    }


    fun changeCategory(category: Category?) {
        category?.let {
            this.currentCategoryId = category.id
            this.category = category.mainName
            _stateFlow.value = AddEditWordUi.CategoryChanged(category.mainName)
        }
    }

    fun onSaveClick(): Boolean {
        if (sourceWord.isBlank() || targetWord.isBlank()) {
            makeToast(R.string.name_translation_not_empty)
            return false
        }
        if (currentCategoryId==null) {
            makeToast(R.string.choose_category)
            return false
        }
        if (word == null) {
            launchIO {
                val word = Word(
                    name = sourceWord,
                    translations = targetWord.split(", "),
                    imageUrl = imageUrl ?: "",
                    examples = exampleText.split("\n"),
                    learnOrKnown = LearnOrKnown.UNDEFINED.getType(),
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    categoryID = currentCategoryId ?: "default",
                )
                firebaseRepository.addWord(word)
            }
        } else {
            launchIO {
                val uWord = word.copy(
                    name = sourceWord,
                    translations = targetWord.split(", "),
                    examples = exampleText.split("\n"),
                    categoryID = currentCategoryId ?: "default",
                    learnOrKnown = LearnOrKnown.UNDEFINED.getType(),
                    imageUrl = imageUrl ?: "",
                    repeatCount = 0
                )
                firebaseRepository.deleteWord(word.categoryID, word.wordId)
                firebaseRepository.addWord(uWord)
            }
        }
        return true
    }

    private fun isItMoreThanOneWord(fromSource: Boolean): Boolean {
        return ((fromSource && sourceWord.trim().split(" ").size <= 1)
                || (!fromSource && targetLanguage.trim().split(" ").size <= 1))
    }

    private fun checkIfOxfordSupport(isFromSource: Boolean): Boolean {
        val map = mapOf(
            "en" to listOf("ar", "zh", "de", "it", "ru"),
            "ar" to listOf("en"),
            "zh" to listOf("en"),
            "de" to listOf("en"),
            "it" to listOf("en"),
            "ru" to listOf("en")
        )
        return if (isFromSource) {
            map[sourceLanguage]?.contains(targetLanguage) ?: false
        } else {
            map[targetLanguage]?.contains(sourceLanguage) ?: false
        }

    }

}

sealed class AddEditWordUi {
    object Default : AddEditWordUi()
    data class Loading(val fromSource: Boolean) : AddEditWordUi()
    data class SuccessYandex(val value: String, val fromSource: Boolean) : AddEditWordUi()
    data class SuccessOxford(val value: ResultsEntry, val fromSource: Boolean) :
        AddEditWordUi()

    data class CategoryChanged(val category: String) : AddEditWordUi()
    data class Error(val errorStatus: ErrorStatus?, val text: String?) : AddEditWordUi()
}