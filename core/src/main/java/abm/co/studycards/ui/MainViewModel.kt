package abm.co.studycards.ui

import abm.co.domain.prefs.Prefs
import abm.co.navigation.Destinations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val prefs: Prefs
) : ViewModel() {

    private val mutableState =
        MutableStateFlow(MainContractState(startDestination = getStartDestination()))
    val state: StateFlow<MainContractState> = mutableState.asStateFlow()

    val channel: Flow<Nothing> get() = emptyFlow()

    fun event(event: MainContractEvent) = when (event) {
        MainContractEvent.OnRefresh -> {

        }
    }

    private fun getStartDestination(): String {
        val hasUser = firebaseAuth.currentUser != null
        return if (hasUser) {
            if (prefs.getNativeLanguage() == null || prefs.getLearningLanguage() == null) {
                Destinations.ChooseUserAttributes.route
            } else Destinations.Home.route
        } else {
            Destinations.WelcomeLogin.route
        }
    }
}

data class MainContractState(val startDestination: String)

sealed class MainContractEvent {
    object OnRefresh : MainContractEvent()
}