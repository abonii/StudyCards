package abm.co.feature.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class ProfileViewModel @Inject constructor(
) : ViewModel() {

    val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(ProfileContractState())
    val state: StateFlow<ProfileContractState> = mutableState.asStateFlow()

    fun event(event: ProfileContractEvent) = when (event) {

        else -> {}
    }
}

data class ProfileContractState(
    val showFavoriteList: Boolean = false
)

sealed interface ProfileContractEvent {
}