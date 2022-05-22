package abm.co.studycards.ui.explore

import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.TAG
import abm.co.studycards.util.base.BaseViewModel
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
        viewModelScope.launch(dispatcher) {
            firebaseRepository.fetchExploreSets().collectLatest {
                Log.i(TAG, "fetched ${it.size}")
                _stateFlow.value = ParentExploreUiState.Success(it)
            }
        }
    }

}

sealed class ParentExploreUI {
    data class SetUI(
        val value: List<ChildExploreVHUI.VHCategory>,
        val title: String,
        val setId: String
    ) :
        ParentExploreUI()
}

sealed class ParentExploreUiState {
    data class Success(val value: List<ParentExploreUI>) : ParentExploreUiState()
    data class Error(@StringRes val error: Int) : ParentExploreUiState()
    object Loading : ParentExploreUiState()
}
