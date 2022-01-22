package abm.co.studycards.ui.learn.select

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.FirebaseRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class SelectLearnTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {
    val category = savedStateHandle.get<Category>("category")!!
    val currentCategory = categoriesDbRef.child(category.id).child("words")
    val categoryName = category.mainName
    var undefinedWordsListLive = MutableLiveData<MutableList<Word>>()
    var unlearnedWordsListLive = MutableLiveData<MutableList<Word>>()
    var repeatWordsLive = MutableLiveData<MutableList<Word>>()
    val allWordsListLive = MutableLiveData<MutableList<Word>>()
    var undefinedWordsList = mutableListOf<Word>()
    var unlearnedWordsList = mutableListOf<Word>()
    var repeatWordsList = mutableListOf<Word>()
    var allWordsList = mutableListOf<Word>()

    fun getLearnWordsInTypedArray(): Array<Word> {
        return undefinedWordsList.toTypedArray()
    }

    fun getRepeatWordsInTypedArray(): Array<Word> {
        return repeatWordsList.toTypedArray()
    }

    fun getAllWordsInTypedArray(): Array<Word> {
        return allWordsList.toTypedArray()
    }

    fun getUnlearnedWordsInTypedArray(): Array<Word> {
        return unlearnedWordsList.toTypedArray()
    }

    fun getTextForLearnSubtitle(context: Context, size: Int): String {
        return context.resources.getQuantityString(R.plurals.learn_words, size, size)

    }

    fun getTextForRepeatSubtitle(context: Context, size: Int): String {
        return context.resources.getQuantityString(R.plurals.repeat_words, size, size)
    }
}