package abm.co.studycards.ui.in_category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.base.BaseViewModel
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class InCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
    prefs: Prefs,
) : BaseViewModel() {
    var category = savedStateHandle.get<Category>("category")!!
    var categoryLiveData = MutableLiveData(category)
    val thisCategoryRef = categoriesDbRef.child(category.id)
    val wordsRef = thisCategoryRef.child(WORDS_REF)
    val targetLang = prefs.getTargetLanguage()

    fun insertWord(word: Word) {
        launchIO {
            addWord(word)
        }
    }

    fun deleteWord(word: Word) {
        launchIO {
            deleteWord(category.id, word.wordId)
        }
    }

    private suspend fun deleteWord(categoryId: String, wordId: String) {
        safeApiCall(Dispatchers.IO) {
            categoriesDbRef.child(categoryId).child(WORDS_REF).child(wordId).removeValue()
        }
    }

    private suspend fun addWord(word: Word) {
        withContext(Dispatchers.IO) {
            val ref = categoriesDbRef.child(word.categoryID).child(WORDS_REF).push()
            ref.setValue(word.copy(wordId = ref.key ?: ""))
        }
    }

    fun makeSnackBarText(name: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<b>$name</b> removed", HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml("<b>$name</b> removed")
        }
    }
}