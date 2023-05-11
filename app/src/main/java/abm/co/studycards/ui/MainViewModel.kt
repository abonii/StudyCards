package abm.co.studycards.ui

import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.StoreRepository
import abm.co.studycards.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val languagesRepository: LanguagesRepository,
    storeRepository: StoreRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow(MainContractState())
    val state: StateFlow<MainContractState> = mutableState.asStateFlow()

    private val _startDestination = MutableStateFlow<Int?>(null)
    val startDestination: StateFlow<Int?> = _startDestination

    init {
        fetchStartDestination()
        storeRepository.startConnection()
    }

    private fun fetchStartDestination() {
        viewModelScope.launch {
            mutableState.update {
                val hasUser = firebaseAuth.currentUser != null
                val startDestination = if (hasUser) {
                    val languagesNotStored = combine(
                        languagesRepository.getLearningLanguage(),
                        languagesRepository.getNativeLanguage()
                    ) { learning, native ->
                        learning == null || native == null
                    }.firstOrNull() ?: true
                    if (languagesNotStored) {
                        R.navigation.root_user_preference_and_language_nav_graph
                    } else {
                        R.navigation.root_main_nav_graph
                    }
                } else {
                    R.navigation.root_authorization_nav_graph
                }
                _startDestination.value = startDestination
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