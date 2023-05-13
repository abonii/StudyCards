package abm.co.feature.changelanguage

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.repository.LanguagesRepository
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.defaultLanguages
import abm.co.feature.userattributes.lanugage.toDomain
import abm.co.feature.userattributes.lanugage.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeLanguageViewModel @Inject constructor(
    private val languagesRepository: LanguagesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ChangeLanguageContractState> =
        MutableStateFlow(ChangeLanguageContractState.Loading)
    val state: StateFlow<ChangeLanguageContractState> = _state.asStateFlow()

    private val _channel = Channel<ChangeLanguageContractChannel>()
    val channel = _channel.receiveAsFlow()

    init {
        fetchLanguages()
    }

    fun onEvent(event: ChangeLanguageContractEvent) {
        when (event) {
            ChangeLanguageContractEvent.OnBack -> {
                _channel.trySend(ChangeLanguageContractChannel.OnBack)
            }

            is ChangeLanguageContractEvent.OnClickLearningLanguage -> {
                onSelectLearningLanguage(event.lang)
            }

            is ChangeLanguageContractEvent.OnClickNativeLanguage -> {
                onSelectNativeLanguage(event.lang)
            }
        }
    }

    private fun fetchLanguages() {
        combine(
            languagesRepository.getNativeLanguage(),
            languagesRepository.getLearningLanguage()
        ) { native, learning ->
            _state.value = ChangeLanguageContractState.Success(
                nativeLanguages = defaultLanguages.toImmutableList(),
                learningLanguages = defaultLanguages.toImmutableList(),
                selectedNativeLanguage = native?.toUI(),
                selectedLearningLanguage = learning?.toUI()
            )
        }.launchIn(viewModelScope)
    }

    private fun onSelectNativeLanguage(lang: LanguageUI) {
        viewModelScope.launch {
            languagesRepository.setNativeLanguage(language = lang.toDomain())
        }
    }

    private fun onSelectLearningLanguage(lang: LanguageUI) {
        viewModelScope.launch {
            languagesRepository.setLearningLanguage(language = lang.toDomain())
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(ChangeLanguageContractChannel.ShowMessage(it))
            }
        }
    }
}


@Stable
sealed interface ChangeLanguageContractState {
    @Immutable
    object Loading : ChangeLanguageContractState

    @Immutable
    data class Success(
        val selectedNativeLanguage: LanguageUI?,
        val selectedLearningLanguage: LanguageUI?,
        val learningLanguages: ImmutableList<LanguageUI>,
        val nativeLanguages: ImmutableList<LanguageUI>
    ) : ChangeLanguageContractState
}

@Stable
sealed interface ChangeLanguageContractEvent {
    @Immutable
    object OnBack : ChangeLanguageContractEvent

    @Immutable
    data class OnClickNativeLanguage(val lang: LanguageUI) : ChangeLanguageContractEvent

    @Immutable
    data class OnClickLearningLanguage(val lang: LanguageUI) : ChangeLanguageContractEvent
}

@Stable
sealed interface ChangeLanguageContractChannel {
    @Immutable
    object OnBack : ChangeLanguageContractChannel

    @Immutable
    data class ShowMessage(val messageContent: MessageContent) : ChangeLanguageContractChannel
}
