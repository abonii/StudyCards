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
    firebaseRepository: ServerCloudRepository
) : BaseViewModel() {
    val category = savedStateHandle.get<Category>("category")!!
    private val categoriesDbRef = firebaseRepository.getCategoriesReference()
    private val currentCategory = categoriesDbRef.child(category.id).child("words")
    val categoryName = category.mainName
    private var leftHours = 0L
    var undefinedWordsListLive = MutableLiveData<MutableList<Word>>()
    val repeatAvailableInCalendar = MutableLiveData<Long>()
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
                    clearAllLists()
                    setupNecessaryWords(snapshot)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setupNecessaryWords(snapshot: DataSnapshot) {
        snapshot.children.forEach { words ->
            setupWord(words)
        }
        if (leftHours > Calendar.getInstance().timeInMillis && repeatWordsList.isEmpty()) {
            repeatAvailableInCalendar.postValue(leftHours)
        }
        allWordsListLive.postValue(allWordsList)
        repeatWordsLive.postValue(repeatWordsList)
        undefinedWordsListLive.postValue(undefinedWordsList)
        unlearnedWordsListLive.postValue(unlearnedWordsList)
    }

    var countForNextRepeat = 0
    private fun setupWord(words: DataSnapshot) {
        words.getValue(Word::class.java)?.let { word ->
            val currentTime = Calendar.getInstance().timeInMillis
            val type = LearnOrKnown.getType(word.learnOrKnown)
            if (LearnOrKnown.UNCERTAIN == type || LearnOrKnown.UNKNOWN == type) {
                if (word.nextRepeatTime <= currentTime) {
                    repeatWordsList.add(word)
                }
                if (currentTime < word.nextRepeatTime) {
                    if (countForNextRepeat++ == 0)
                        leftHours = word.nextRepeatTime
                    else if (leftHours > word.nextRepeatTime) {
                        leftHours = word.nextRepeatTime
                    }
                }
                unlearnedWordsList.add(word)
            }
            if (LearnOrKnown.UNDEFINED == type) {
                undefinedWordsList.add(word)
            }
            allWordsList.add(word)
        }
    }

    private fun clearAllLists() {
        allWordsList.clear()
        unlearnedWordsList.clear()
        repeatWordsList.clear()
        undefinedWordsList.clear()
    }

    fun getLearnWordsInTypedArray(): Array<Word> {
        return undefinedWordsList.take(50).toTypedArray()
    }

    fun getRepeatWordsInTypedArray(): Array<Word> {
        return repeatWordsList.toTypedArray()
    }

    fun getAllWordsInTypedArray(): Array<Word> {
        return allWordsList.toTypedArray()
    }

    fun getUnlearnedWordsInTypedArray(): Array<Word> {
        return (unlearnedWordsList + undefinedWordsList).toTypedArray()
    }

    fun getTextForLearnSubtitle(context: Context, size: Int): String {
        return context.resources.getQuantityString(R.plurals.learn_words, size, size)
    }

    fun getTextForRepeatSubtitle(context: Context, size: Int): String {
        return context.resources.getQuantityString(R.plurals.repeat_words, size, size)
    }

    fun getTextForRepeatTimeHour(context: Context, size: Int): String {
        return context.resources.getQuantityString(R.plurals.repeat_time_hour, size, size)
    }

    fun getTextForRepeatTimeMinute(context: Context, size: Int): String {
        return context.resources.getQuantityString(R.plurals.repeat_time_minute, size, size)
    }
}