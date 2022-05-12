package abm.co.studycards.ui.explore

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.NAME_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    firebaseRepository: ServerCloudRepository,
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<ParentExploreUiState>(ParentExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        firebaseRepository.getExploreReference()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch(dispatcher) {
                        val items = mutableListOf<ParentExploreUI>()
                        snapshot.children.forEach { set ->
                            val sets1 = mutableListOf<Category>()
                            var setName: String
                            var setId: String
                            viewModelScope.launch(dispatcher) {
                                setName = set.child(NAME_REF).getValue<String>().toString()
                                setId = set.key.toString()
                                viewModelScope.launch(dispatcher) {
                                    set.child(CATEGORIES_REF).children.forEach {
                                        it.getValue(Category::class.java)?.let { it1 ->
                                            sets1.add(it1)
                                        }
                                    }
                                }.join()
                                items.add(
                                    ParentExploreUI.SetUI(
                                        sets1.map {
                                            ChildExploreVHUI.VHCategory(it)
                                        }, setName, setId
                                    )
                                )
                            }.join()
                        }
                        delay(300)
                        if (items.isNotEmpty()) {
                            _stateFlow.value = ParentExploreUiState.Success(items)
                        } else {
                            _stateFlow.value =
                                ParentExploreUiState.Error(R.string.empty)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateFlow.value = ParentExploreUiState.Error(firebaseError(error.code))
                }
            })
    }

}

sealed class ParentExploreUI {
    data class SetUI(val value: List<ChildExploreVHUI.VHCategory>, val title: String, val setId:String) :
        ParentExploreUI()
}

sealed class ParentExploreUiState {
    data class Success(val value: List<ParentExploreUI>) : ParentExploreUiState()
    data class Error(@StringRes val error: Int) : ParentExploreUiState()
    object Loading : ParentExploreUiState()
}
