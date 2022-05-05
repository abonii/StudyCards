package abm.co.studycards.ui.games.select

import abm.co.studycards.R
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class SelectLearnTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {
    val category = savedStateHandle.get<Category>("category")!!
    val categoriesDbRef = firebaseRepository.getCategoriesReference()
    private val currentCategory = categoriesDbRef.child(category.id).child("words")
    val categoryName = category.mainName
    var undefinedWordsListLive = MutableLiveData<MutableList<Word>>()
    var unlearnedWordsListLive = MutableLiveData<MutableList<Word>>()
    var repeatWordsLive = MutableLiveData<MutableList<Word>>()
    val allWordsListLive = MutableLiveData<MutableList<Word>>()
    var undefinedWordsList = mutableListOf<Word>()
    var unlearnedWordsList = mutableListOf<Word>()
    var repeatWordsList = mutableListOf<Word>()
    var allWordsList = mutableListOf<Word>()

    init {
        currentCategory.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(Dispatchers.IO) {
                    allWordsList.clear()
                    unlearnedWordsList.clear()
                    repeatWordsList.clear()
                    undefinedWordsList.clear()
                    snapshot.children.forEach { words ->
                        words.getValue(Word::class.java)?.let { word ->
                            val currentTime = Calendar.getInstance().timeInMillis
                            val type = LearnOrKnown.getType(word.learnOrKnown)
                            if (LearnOrKnown.UNCERTAIN == type || LearnOrKnown.UNKNOWN == type) {
                                if (word.nextRepeatTime <= currentTime)
                                    repeatWordsList.add(word)
                                unlearnedWordsList.add(word)
                            }
                            if (LearnOrKnown.UNDEFINED == type) {
                                undefinedWordsList.add(word)
                            }
                            allWordsList.add(word)
                        }

                    }
                    allWordsListLive.postValue(allWordsList)
                    repeatWordsLive.postValue(repeatWordsList)
                    undefinedWordsListLive.postValue(undefinedWordsList)
                    unlearnedWordsListLive.postValue(unlearnedWordsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

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