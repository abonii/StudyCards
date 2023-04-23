package abm.co.feature.userattributes

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.LanguagesRepository
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
import androidx.lifecycle.SavedStateHandle
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
class UserPreferenceAndLanguageViewModel @Inject constructor(
    private val languagesRepository: LanguagesRepository,
    private val repository: AuthorizationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val showAdditionQuiz = savedStateHandle["show_addition_quiz"] ?: false

    private val mutableState = MutableStateFlow(
        ChooseUserPreferenceAndLanguageContractState(showAdditionQuiz = showAdditionQuiz)
    )
    val state: StateFlow<ChooseUserPreferenceAndLanguageContractState> = mutableState.asStateFlow()

    private val _channel = Channel<UserPreferenceAndLanguageContractChannel>()
    val channel = _channel.receiveAsFlow()

    fun event(event: ChooseUserPreferenceAndLanguageContractEvent) {
        when (event) {
            is ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToUserGoal -> {
                onNavigateToUserGoal(event.learningLanguage, event.isToRight)
            }
            is ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToLearningLanguage -> {
                onNavigateToLearningLanguage(event.nativeLanguage, event.isToRight)
            }
            is ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToUserInterests -> {
                onNavigateToUserInterests(event.userGoal)
            }
            is ChooseUserPreferenceAndLanguageContractEvent.OnSelectUserInterest -> {
                onToggleUserInterests(event.userInterestUI)
            }
            ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToNativeLanguage -> {
                onNavigateToNativeLanguage()
            }
            is ChooseUserPreferenceAndLanguageContractEvent.OnFinish -> {
                event.learningLanguage?.let { saveLearningLanguage(it) }
                onClickContinue()
            }
        }
    }

    private fun onNavigateToNativeLanguage() {
        mutableState.update {
            it.copy(
                currentPage = UserPreferenceAndLanguagePage.NativeLanguage,
                progress = 0.2f,
                isToRight = false
            )
        }
    }

    private fun onNavigateToLearningLanguage(nativeLanguage: LanguageUI?, toRight: Boolean) {
        nativeLanguage?.let { saveNativeLanguage(it) }
        mutableState.update {
            it.copy(
                currentPage = UserPreferenceAndLanguagePage.LearningLanguage,
                progress = if (showAdditionQuiz) 0.4f else 0.6f,
                isToRight = toRight
            )
        }
    }

    private fun onNavigateToUserGoal(learningLanguage: LanguageUI?, toRight: Boolean) {
        learningLanguage?.let { saveLearningLanguage(it) }
        mutableState.update {
            it.copy(
                currentPage = UserPreferenceAndLanguagePage.UserGoal,
                progress = 0.6f,
                isToRight = toRight
            )
        }
    }

    private fun onNavigateToUserInterests(userGoal: UserGoalUI?) {
        userGoal?.let { saveUserGoal(it) }
        mutableState.update {
            it.copy(
                currentPage = UserPreferenceAndLanguagePage.UserInterests,
                progress = 0.8f,
                isToRight = true
            )
        }
    }

    private fun onClickContinue() {
        viewModelScope.launch {
            mutableState.update { contractState -> contractState.copy(progress = 1f) }
            repository.setUserInterests(
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
            repository.setUserGoal(userGoal.toDomain())
        }
    }

    private fun saveNativeLanguage(language: LanguageUI) {
        viewModelScope.launch {
            languagesRepository.setNativeLanguage(language.toDomain())
        }
    }

    private fun saveLearningLanguage(language: LanguageUI) {
        viewModelScope.launch {
            languagesRepository.setLearningLanguage(language.toDomain())
        }
    }

    private fun navigateToHomePage() {
        viewModelScope.launch {
            _channel.send(UserPreferenceAndLanguageContractChannel.NavigateToHomePage)
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(UserPreferenceAndLanguageContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class ChooseUserPreferenceAndLanguageContractState(
    val progress: Float = 0.2f, // 0..1
    val showAdditionQuiz: Boolean,
    val currentPage: UserPreferenceAndLanguagePage = UserPreferenceAndLanguagePage.NativeLanguage,
    val languages: ImmutableList<LanguageUI> = defaultLanguages.toImmutableList(),
    val userGoals: ImmutableList<UserGoalUI> = defaultUserGoals.toImmutableList(),
    val userInterests: ImmutableList<UserInterestUI> = defaultUserInterests.toImmutableList(),
    val isToRight: Boolean = true
)

@Immutable
sealed interface ChooseUserPreferenceAndLanguageContractEvent {
    data class OnFinish(val learningLanguage: LanguageUI?) : ChooseUserPreferenceAndLanguageContractEvent
    object OnNavigateToNativeLanguage : ChooseUserPreferenceAndLanguageContractEvent
    data class OnSelectUserInterest(val userInterestUI: UserInterestUI) :
        ChooseUserPreferenceAndLanguageContractEvent

    data class OnNavigateToLearningLanguage(
        val nativeLanguage: LanguageUI?,
        val isToRight: Boolean
    ) : ChooseUserPreferenceAndLanguageContractEvent

    data class OnNavigateToUserGoal(
        val learningLanguage: LanguageUI?,
        val isToRight: Boolean
    ) : ChooseUserPreferenceAndLanguageContractEvent

    data class OnNavigateToUserInterests(val userGoal: UserGoalUI?) :
        ChooseUserPreferenceAndLanguageContractEvent
}

@Immutable
sealed interface UserPreferenceAndLanguageContractChannel {
    object NavigateToHomePage : UserPreferenceAndLanguageContractChannel
    data class ShowMessage(val messageContent: MessageContent) : UserPreferenceAndLanguageContractChannel
}