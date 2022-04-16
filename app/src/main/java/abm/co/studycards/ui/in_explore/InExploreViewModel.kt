package abm.co.studycards.ui.in_explore

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class InExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(EXPLORE_REF) val exploreDbRef: DatabaseReference,
    @Named(CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<InExploreUiState>(InExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    var category = savedStateHandle.get<Category>("category")!!

    val categoryPhotoUrl = category.imageUrl

    private val wordsRef = exploreDbRef.child(CATEGORIES_REF).child(category.id).child(WORDS_REF)

    val targetLang = prefs.getTargetLanguage()

    val isThisCategoryExistInMine = MutableStateFlow(true)

    var wordsCount = category.words.size

    init {
        categoriesDbRef.child(category.id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isThisCategoryExistInMine.value = snapshot.exists()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        wordsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(dispatcher) {
                    val items = mutableListOf<Word>()
                    snapshot.children.forEach {
                        it.getValue(Word::class.java)?.let { it1 -> items.add(it1) }
                    }
                    delay(300)
                    _stateFlow.value = InExploreUiState.Success(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = InExploreUiState.Error(error.message)
            }
        })
    }

}

sealed class InExploreUiState {
    data class Success(val value: List<Word>) : InExploreUiState()
    object Loading : InExploreUiState()
    data class Error(val msg: String) : InExploreUiState()
}