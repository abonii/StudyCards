package abm.co.studycards.ui

import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.StoreRepository
import abm.co.studycards.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val languagesRepository: LanguagesRepository,
    storeRepository: StoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainContractState())
    val state: StateFlow<MainContractState> = _state.asStateFlow()

    private val _startDestination = Channel<Int>()
    val startDestination: Flow<Int> = _startDestination.receiveAsFlow()

    init {
        fetchStartDestination()
        storeRepository.startConnection()
    }

    private fun fetchStartDestination() {
        viewModelScope.launch {
            _state.update {
                val hasUser = firebaseAuth.currentUser != null
                val startDestination = if (hasUser) {
                    val languagesNotStored = combine(
                        languagesRepository.getLearningLanguage(),
                        languagesRepository.getNativeLanguage()
                    ) { learning, native ->
                        learning == null || native == null
                    }.firstOrNull() ?: true
                    if (languagesNotStored) {
                        R.id.root_user_preference_and_language_nav_graph
                    } else {
                        R.id.root_main_nav_graph
                    }
                } else {
                    R.id.root_authorization_nav_graph
                }
                _state.update { oldState ->
                    oldState.copy(isSplashScreenVisible = true)
                }
                _startDestination.send(startDestination)
                delay(50) // status bar height cannot be calculated immediately
                it.copy(isSplashScreenVisible = false)
            }
        }
    }
}

data class MainContractState(
    val isSplashScreenVisible: Boolean = true
)

sealed class MainContractEvent {
    object OnRefresh : MainContractEvent()
}