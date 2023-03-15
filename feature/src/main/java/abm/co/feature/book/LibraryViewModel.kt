package abm.co.feature.book

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class LibraryViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(LibraryContractState())
    val state: StateFlow<LibraryContractState> = mutableState.asStateFlow()

    fun event(event: LibraryContractEvent) = when (event) {

        else -> {}
    }
}

data class LibraryContractState(
    val showFavoriteList: Boolean = false
)

sealed interface LibraryContractEvent {
}