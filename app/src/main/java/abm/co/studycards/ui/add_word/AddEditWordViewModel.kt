package abm.co.studycards.ui.add_word

import abm.co.studycards.data.ErrorStatus
import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.VocabularyRepository
import abm.co.studycards.util.base.BaseViewModel
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    private val repository: VocabularyRepository,
    prefs: Prefs,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val isLanguagesSupportOxford: Boolean = true
    val word = savedStateHandle.get<Word>("word")
    var category = savedStateHandle.get<Category>("category")
    var imageUrl: String? = word?.imageUrl
    var sourceWord: String = word?.name ?: ""
    var targetWord: String = word?.translations?.joinToString(", ") ?: ""
    var exampleText: String = word?.examples?.joinToString(", ") ?: ""
    val sourceLanguage = prefs.getSourceLanguage()
    val targetLanguage = prefs.getTargetLanguage()
    private var translatedOxfordWord: String = "87716944499"
    private var translatedYandexWord: String = ""
    private lateinit var translatedOxfordClass: ResultsEntry
    private lateinit var translatedYandexClass: String


    private val _stateFlow = MutableStateFlow<AddEditWordUi>(AddEditWordUi.Default)
    val stateFlow = _stateFlow.asStateFlow()

    fun fetchWord(fromSource: Boolean) {
        if (isLanguagesSupportOxford && isItMoreThanOneWord(fromSource)) {
            fetchOxfordWord(fromSource)
        } else fetchYandexWord(fromSource)
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
                    Log.i("addeditlogYan", wrapper.error.toString())
                }
                is ResultWrapper.Success -> {
                    translatedYandexClass = wrapper.value.text?.joinToString(", ") ?: ""
                    _stateFlow.value =
                        AddEditWordUi.SuccessYandex(translatedYandexClass, fromSource)
                }
            }
        }
    }

    private fun fetchOxfordWord(fromSource: Boolean) {
        launchIO {
            _stateFlow.value = AddEditWordUi.Loading(fromSource)
            delay(50)
            if ((fromSource && isSourceWordTranslatedOxford()) || (!fromSource && isTargetWordTranslatedOxford())) {
                _stateFlow.value = AddEditWordUi.SuccessOxford(translatedOxfordClass, fromSource)
                return@launchIO
            }
            val sl = if (fromSource) sourceLanguage else targetLanguage
            val tl = if (fromSource) targetLanguage else sourceLanguage

            translatedOxfordWord = if (fromSource) sourceWord else targetWord

            when (val wrapper = repository.getOxfordWord(translatedOxfordWord.trim(), sl, tl)) {
                is ResultWrapper.Error -> {
                    fetchYandexWord(fromSource)
                }
                is ResultWrapper.Success -> {
                    translatedOxfordClass = wrapper.value.results[0]
                    _stateFlow.value =
                        AddEditWordUi.SuccessOxford(translatedOxfordClass, fromSource)
                }
            }
        }
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
            this.category = category
            _stateFlow.value = AddEditWordUi.CategoryChanged(category.mainName)
        }
    }

    fun onSaveClick(): Boolean {
        if (sourceWord.isBlank() || targetLanguage.isBlank()) {
            makeToast("Name or Translation cannot be empty")
            return false
        }
        if (category == null) {
            makeToast("Please, create category for this word")
            return false
        }
        val word = Word(
            name = sourceWord,
            translations = targetWord.split(", "),
            imageUrl = imageUrl ?: "",
            examples = exampleText.split(", "),
            learnOrKnown = LearnOrKnown.UNDEFINED.getType(),
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            categoryID = category?.id ?: "",
        )
        launchIO {
            repository.addWord(word)
        }
        return true
    }
    private fun isItMoreThanOneWord(fromSource: Boolean): Boolean {
        return ((fromSource && sourceWord.trim().split(" ").size <= 1)
                || (!fromSource && targetLanguage.trim().split(" ").size <= 1))
    }

}

sealed class AddEditWordUi {
    object Default : AddEditWordUi()
    data class Loading(val fromSource: Boolean) : AddEditWordUi()
    data class SuccessYandex(val value: String, val fromSource: Boolean) : AddEditWordUi()
    data class SuccessOxford(val value: ResultsEntry, val fromSource: Boolean) :
        AddEditWordUi()

    data class CategoryChanged(val category: String) : AddEditWordUi()
    data class Error(val errorStatus: ErrorStatus?) : AddEditWordUi()
}