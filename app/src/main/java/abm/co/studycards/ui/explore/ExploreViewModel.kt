package abm.co.studycards.ui.explore

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.SETS_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import android.util.Log
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
class ExploreViewModel @Inject constructor(
    @Named(EXPLORE_REF)
    var exploreDbRef: DatabaseReference,
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<ParentExploreUiState>(ParentExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        exploreDbRef.child(SETS_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("ABO_EXPLORE", snapshot.value.toString())
                val items = mutableListOf<ParentExploreUI>()
                val sets1 = mutableListOf<Category>()
                viewModelScope.launch(dispatcher) {
                    viewModelScope.launch(dispatcher) {
                        snapshot.children.forEach {
                            it.getValue(Category::class.java)?.let { it1 ->
                                sets1.add(it1)
                            }
                        }
                    }.join()
                    delay(1500)
                    items.add(
                        ParentExploreUI.SetUI(
                            sets1.map {
                                ChildExploreVHUI.VHCategory(it)
                            }, App.instance.getString(R.string.set_of_words)
                        )
                    )
                    if (sets1.isNotEmpty()) {
                        _stateFlow.value = ParentExploreUiState.Success(items)
                    } else {
                        _stateFlow.value =
                            ParentExploreUiState.Error(App.instance.getString(R.string.empty))
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = ParentExploreUiState.Error(error.message)
            }

        })
    }

}

sealed class ParentExploreUI {
    data class SetUI(val value: List<ChildExploreVHUI.VHCategory>, val title: String) :
        ParentExploreUI()
}

sealed class ParentExploreUiState {
    data class Success(val value: List<ParentExploreUI>) : ParentExploreUiState()
    data class Error(val error: String) : ParentExploreUiState()
    object Loading : ParentExploreUiState()
}
