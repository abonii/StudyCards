package abm.co.feature.userattributes

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.feature.userattributes.lanugage.Language
import abm.co.feature.userattributes.usergoal.UserGoal
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChooseUserAttributesViewModel @Inject constructor(

) : ViewModel() {

    private val mutableState = MutableStateFlow(ChooseUserAttributesContractState())
    val state: StateFlow<ChooseUserAttributesContractState> = mutableState.asStateFlow()

    private val _channel = Channel<ChooseUserAttributesContractChannel>()
    val channel = _channel.receiveAsFlow()

    fun event(event: ChooseUserAttributesContractEvent) {
        viewModelScope.launch {
            when (event) {
                ChooseUserAttributesContractEvent.OnClickContinue -> {
                    navigateToHomePage()
                }
                is ChooseUserAttributesContractEvent.OnClickLearningLanguage -> {
                    onClickLearningLanguage(event.language)
                }
                is ChooseUserAttributesContractEvent.OnClickNativeLanguage -> {
                    onClickNativeLanguage(event.language)
                }
                is ChooseUserAttributesContractEvent.OnClickReasonOfLearning -> {
                    onClickReasonOfLearning(event.userGoal, )
                }
                is ChooseUserAttributesContractEvent.OnToggleUserInterests -> {
                    onToggleUserInterests()
                }
            }
        }
    }

    private fun onToggleUserInterests() {
        mutableState.update {
            it.copy(progress = 1f)
        }
        // TODO store
    }

    private fun onClickReasonOfLearning(userGoal: UserGoal) {
        mutableState.update {
            it.copy(progress = 0.75f, currentPage = UserAttributesPage.UserInterests)
        }
        // TODO store reason
    }

    private fun onClickNativeLanguage(language: Language) {
        mutableState.update {
            it.copy(progress = 0.25f, currentPage = UserAttributesPage.LearningLanguage)
        }
        // TODO store language
    }

    private fun onClickLearningLanguage(language: Language) {
        mutableState.update {
            it.copy(progress = 0.5f, currentPage = UserAttributesPage.UserGoal)
        }
        // TODO store language
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
    val progress: Float = 0f, // 0..1
    val currentPage: UserAttributesPage = UserAttributesPage.NativeLanguage
)

@Immutable
sealed interface ChooseUserAttributesContractEvent {
    object OnClickContinue : ChooseUserAttributesContractEvent
    data class OnClickNativeLanguage(val language: Language) : ChooseUserAttributesContractEvent
    data class OnClickLearningLanguage(val language: Language) : ChooseUserAttributesContractEvent
    data class OnClickReasonOfLearning(
        val userGoal: UserGoal
    ) : ChooseUserAttributesContractEvent

    data class OnToggleUserInterests(
        val userGoal: UserGoal
    ) : ChooseUserAttributesContractEvent
}

@Immutable
sealed interface ChooseUserAttributesContractChannel {
    object NavigateToHomePage : ChooseUserAttributesContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ChooseUserAttributesContractChannel
}