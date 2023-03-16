package abm.co.studycards.ui

import abm.co.domain.prefs.Prefs
import abm.co.navigation.graph.root.Graph
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val prefs: Prefs
) : ViewModel() {

    private val mutableState = MutableStateFlow(MainContractState())
    val state: StateFlow<MainContractState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchStartDestination()
            isEverythingReady()
        }
    }

    private suspend fun isEverythingReady() {
        mutableState.update {
            delay(50) // status bar height cannot be calculated immediately
            it.copy(isSplashScreenVisible = it.startDestination == null)
        }
    }

    private fun fetchStartDestination() {
        mutableState.update {
            val hasUser = firebaseAuth.currentUser != null
            val startDestination = if (hasUser) {
                if (prefs.getNativeLanguage() == null || prefs.getLearningLanguage() == null) {
                    Graph.USER_ATTRIBUTES
                } else Graph.MAIN
            } else {
                Graph.AUTH
            }
            it.copy(startDestination = startDestination)
        }
    }
}

data class MainContractState(
    val startDestination: String? = null,
    val isSplashScreenVisible: Boolean = true
)

sealed class MainContractEvent {
    object OnRefresh : MainContractEvent()
}