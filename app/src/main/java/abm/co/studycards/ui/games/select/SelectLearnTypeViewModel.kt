package abm.co.studycards.ui.games.select

import abm.co.studycards.domain.model.LearnOrKnown
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectLearnTypeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    firebaseRepository: ServerCloudRepository
) : BaseViewModel() {
    val categoryId = savedStateHandle.get<String>("category_id")!!
    var categoryName: String = ""
    private var leftHours = 0L
    private var countForNextRepeat = 0
    var undefinedWordsListLive = MutableLiveData<MutableList<Word>>()
    val repeatAvailableInCalendar = MutableLiveData<Long>()
    var unlearnedWordsListLive = MutableLiveData<MutableList<Word>>()
    var repeatWordsLive = MutableLiveData<MutableList<Word>>()
    val allWordsListLive = MutableLiveData<MutableList<Word>>()
    var undefinedWordsList = mutableListOf<Word>()
    var unlearnedWordsList = mutableListOf<Word>()
    var repeatWordsList = mutableListOf<Word>()
    var allWordsList = mutableListOf<Word>()

    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseListener: ValueEventListener

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val theCategory = firebaseRepository.getTheCategory(categoryId)
            databaseRef = theCategory.second
            databaseListener = theCategory.third
            theCategory.first.collectLatest { category ->
                category?.let {
                    categoryName = category.name
                    setupWord(it.words)
                }
            }
        }
    }

    private fun setupWord(words: List<Word>) {
        clearAllLists()
        words.forEach { word ->
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
        setupLiveDataWords()
    }

    private fun setupLiveDataWords() {
        if (leftHours > Calendar.getInstance().timeInMillis && repeatWordsList.isEmpty()) {
            repeatAvailableInCalendar.postValue(leftHours)
        }
        allWordsListLive.postValue(allWordsList)
        repeatWordsLive.postValue(repeatWordsList)
        undefinedWordsListLive.postValue(undefinedWordsList)
        unlearnedWordsListLive.postValue(unlearnedWordsList)
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

    override fun onCleared() {
        super.onCleared()
        databaseListener.let { databaseRef.removeEventListener(it) }
    }

}