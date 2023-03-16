package abm.co.feature.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.prefs.Prefs
import abm.co.domain.repository.ServerRepository
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ServerRepository,
    private val prefs: Prefs
) : ViewModel() {

    private val _channel = Channel<HomeContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val mutableState = MutableStateFlow<HomeContractState>(HomeContractState.Loading)
    val state: StateFlow<HomeContractState> = mutableState.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            repository.getUser().collectLatest { userEither ->
                userEither.onFailure {
                    it.sendException()
                }.onSuccess { user ->
                    if (user != null) {
                        mutableState.value = HomeContractState.Success(
                            userName = user.name,
                            learningLanguage = prefs.getLearningLanguage()?.toUI()
                        )
                    } else {
                        mutableState.value = HomeContractState.Empty()
                    }
                }
            }
        }
    }

    fun event(event: HomeContractEvent) {
        when (event) {
            HomeContractEvent.OnClickDrawer -> openDrawer()
            HomeContractEvent.OnClickToolbarLanguage -> onClickToolbarLanguage()
        }
    }

    private fun openDrawer() {
        viewModelScope.launch { _channel.send(HomeContractChannel.OpenDrawer) }
    }

    private fun onClickToolbarLanguage() {
        viewModelScope.launch { _channel.send(HomeContractChannel.NavigateToLanguageSelectPage) }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(HomeContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
sealed class HomeContractState(
    val userName: String? = null,
    val learningLanguage: LanguageUI? = null
) {

    @Immutable
    object Loading : HomeContractState()

    @Immutable
    class Empty(
        userName: String? = null,
        learningLanguage: LanguageUI? = null
    ) : HomeContractState(userName = userName, learningLanguage = learningLanguage)

    @Immutable
    class Success(
        userName: String? = null,
        learningLanguage: LanguageUI? = null
    ) : HomeContractState(userName = userName, learningLanguage = learningLanguage)
}

sealed interface HomeContractEvent {
    object OnClickDrawer : HomeContractEvent
    object OnClickToolbarLanguage : HomeContractEvent
}

sealed interface HomeContractChannel {
    object OpenDrawer : HomeContractChannel
    object NavigateToLanguageSelectPage : HomeContractChannel
    data class ShowMessage(val messageContent: MessageContent) : HomeContractChannel
}