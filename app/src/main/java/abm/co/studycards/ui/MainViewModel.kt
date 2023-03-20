package abm.co.studycards.ui

import abm.co.domain.base.onSuccess
import abm.co.domain.model.Language
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val languagesRepository: LanguagesRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow(MainContractState())
    val state: StateFlow<MainContractState> = mutableState.asStateFlow()

    init {
        fetchStartDestination()
        listenIsUserHasCategories()
        isEverythingReady()
    }

    private fun listenIsUserHasCategories() {
        viewModelScope.launch {
            serverRepository.getCategories.collectLatest { either ->
                either.onSuccess { category ->
                    mutableState.update {
                        it.copy(
                            startDestinationOfNewCardOrCategory = if (category.isNotEmpty()) {
                                NewCardOrCategoryDestinations.Card
                            } else NewCardOrCategoryDestinations.Category
                        )
                    }
                }
            }
        }
    }

    private fun isEverythingReady() {
        viewModelScope.launch {
            mutableState.update {
                delay(50) // status bar height cannot be calculated immediately
                it.copy(isSplashScreenVisible = it.startDestination == null)
            }
        }
    }

    private fun fetchStartDestination() {
        viewModelScope.launch {
            mutableState.update {
                val hasUser = firebaseAuth.currentUser != null
                val startDestination = if (hasUser) {
                    if (languagesRepository.getNativeLanguage()
                            .firstOrNull() == null || languagesRepository.getLearningLanguage()
                            .firstOrNull() == null
                    ) {
                        Graph.USER_ATTRIBUTES
                    } else Graph.MAIN
                } else {
                    Graph.AUTH
                }
                it.copy(startDestination = startDestination)
            }
        }
    }
}

data class MainContractState(
    val startDestination: String? = null,
    val isSplashScreenVisible: Boolean = true,
    val startDestinationOfNewCardOrCategory: NewCardOrCategoryDestinations = NewCardOrCategoryDestinations.Card
)

sealed class MainContractEvent {
    object OnRefresh : MainContractEvent()
}