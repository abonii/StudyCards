package abm.co.feature.userattributes

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.prefs.Prefs
import abm.co.domain.repository.ServerRepository
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.defaultLanguages
import abm.co.feature.userattributes.lanugage.toDomain
import abm.co.feature.userattributes.usergoal.UserGoalUI
import abm.co.feature.userattributes.usergoal.defaultUserGoals
import abm.co.feature.userattributes.usergoal.toDomain
import abm.co.feature.userattributes.userinterest.UserInterestUI
import abm.co.feature.userattributes.userinterest.defaultUserInterests
import abm.co.feature.userattributes.userinterest.toDomain
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChooseUserAttributesViewModel @Inject constructor(
    private val prefs: Prefs,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow(ChooseUserAttributesContractState())
    val state: StateFlow<ChooseUserAttributesContractState> = mutableState.asStateFlow()

    private val _channel = Channel<ChooseUserAttributesContractChannel>()
    val channel = _channel.receiveAsFlow()

    fun event(event: ChooseUserAttributesContractEvent) {
        when (event) {
            is ChooseUserAttributesContractEvent.OnNavigateToUserGoal -> {
                onNavigateToUserGoal(event.learningLanguage, event.isToRight)
            }
            is ChooseUserAttributesContractEvent.OnNavigateToLearningLanguage -> {
                onNavigateToLearningLanguage(event.nativeLanguage, event.isToRight)
            }
            is ChooseUserAttributesContractEvent.OnNavigateToUserInterests -> {
                onNavigateToUserInterests(event.userGoal)
            }
            is ChooseUserAttributesContractEvent.OnSelectUserInterest -> {
                onToggleUserInterests(event.userInterestUI)
            }
            ChooseUserAttributesContractEvent.OnNavigateToNativeLanguage -> {
                onNavigateToNativeLanguage()
            }
            ChooseUserAttributesContractEvent.OnClickContinue -> {
                onClickContinue()
            }
        }
    }

    private fun onNavigateToNativeLanguage() {
        mutableState.update {
            it.copy(
                currentPage = UserAttributesPage.NativeLanguage,
                progress = 0.2f,
                isToRight = false
            )
        }
    }

    private fun onNavigateToLearningLanguage(nativeLanguage: LanguageUI?, toRight: Boolean) {
        nativeLanguage?.let { saveNativeLanguage(it) }
        mutableState.update {
            it.copy(
                currentPage = UserAttributesPage.LearningLanguage,
                progress = 0.4f,
                isToRight = toRight
            )
        }
    }

    private fun onNavigateToUserGoal(learningLanguage: LanguageUI?, toRight: Boolean) {
        learningLanguage?.let { saveLearningLanguage(it) }
        mutableState.update {
            it.copy(
                currentPage = UserAttributesPage.UserGoal,
                progress = 0.6f,
                isToRight = toRight
            )
        }
    }

    private fun onNavigateToUserInterests(userGoal: UserGoalUI?) {
        userGoal?.let { saveUserGoal(it) }
        mutableState.update {
            it.copy(
                currentPage = UserAttributesPage.UserInterests,
                progress = 0.8f,
                isToRight = true
            )
        }
    }

    private fun onClickContinue() {
        viewModelScope.launch {
            mutableState.update { contractState -> contractState.copy(progress = 1f) }
            serverRepository.setUserInterests(
                state.value.userInterests.filter { it.isSelected }.map { it.toDomain() })
            delay(500)
            navigateToHomePage()
        }
    }

    private fun onToggleUserInterests(userInterest: UserInterestUI) {
        mutableState.update { contractState ->
            contractState.copy(
                userInterests = contractState.userInterests.map {
                    if (userInterest.id == it.id) {
                        it.copy(isSelected = !userInterest.isSelected)
                    } else it
                }.toImmutableList()
            )
        }
    }

    private fun saveUserGoal(userGoal: UserGoalUI) {
        viewModelScope.launch {
            serverRepository.setUserGoal(userGoal.toDomain())
        }
    }

    private fun saveNativeLanguage(language: LanguageUI) {
        prefs.setNativeLanguage(language.toDomain())
    }

    private fun saveLearningLanguage(language: LanguageUI) {
        prefs.setLearningLanguage(language.toDomain())
    }

    private fun navigateToHomePage() {
        viewModelScope.launch {
            _channel.send(ChooseUserAttributesContractChannel.NavigateToHomePage)
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(ChooseUserAttributesContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class ChooseUserAttributesContractState(
    val progress: Float = 0.2f, // 0..1
    val currentPage: UserAttributesPage = UserAttributesPage.NativeLanguage,
    val languages: ImmutableList<LanguageUI> = defaultLanguages.toImmutableList(),
    val userGoals: ImmutableList<UserGoalUI> = defaultUserGoals.toImmutableList(),
    val userInterests: ImmutableList<UserInterestUI> = defaultUserInterests.toImmutableList(),
    val isToRight: Boolean = true
)

@Immutable
sealed interface ChooseUserAttributesContractEvent {
    object OnClickContinue : ChooseUserAttributesContractEvent
    object OnNavigateToNativeLanguage : ChooseUserAttributesContractEvent
    data class OnSelectUserInterest(val userInterestUI: UserInterestUI) :
        ChooseUserAttributesContractEvent

    data class OnNavigateToLearningLanguage(
        val nativeLanguage: LanguageUI?,
        val isToRight: Boolean
    ) :
        ChooseUserAttributesContractEvent

    data class OnNavigateToUserGoal(
        val learningLanguage: LanguageUI?,
        val isToRight: Boolean
    ) :
        ChooseUserAttributesContractEvent

    data class OnNavigateToUserInterests(val userGoal: UserGoalUI?) :
        ChooseUserAttributesContractEvent
}

@Immutable
sealed interface ChooseUserAttributesContractChannel {
    object NavigateToHomePage : ChooseUserAttributesContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ChooseUserAttributesContractChannel
}