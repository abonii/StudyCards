package abm.co.feature.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.model.Language
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.defaultLanguages
import abm.co.feature.userattributes.lanugage.toDomain
import abm.co.feature.userattributes.lanugage.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ServerRepository,
    private val languagesRepository: LanguagesRepository
) : ViewModel() {

    private val _channel = Channel<HomeContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val mutableScreenState =
        MutableStateFlow<HomeContract.ScreenState>(HomeContract.ScreenState.Loading)
    val screenState: StateFlow<HomeContract.ScreenState> = mutableScreenState.asStateFlow()

    private val mutableToolbarState = MutableStateFlow(HomeContract.ToolbarState())
    val toolbarState: StateFlow<HomeContract.ToolbarState> = mutableToolbarState.asStateFlow()

    init {
        fetchUser()
        fetchCategories()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            repository.getUser.collectLatest { userEither ->
                userEither.onFailure {
                    it.sendException()
                }.onSuccess { user ->
                    if (user != null) {
                        mutableToolbarState.value = HomeContract.ToolbarState(
                            userName = user.name ?: user.email,
                            learningLanguage = languagesRepository.getLearningLanguage()
                                .firstOrNull()?.toUI()
                        )
                    }
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            repository.getCategories.collectLatest { setsOfCardsEither ->
                setsOfCardsEither.onFailure {
                    it.sendException()
                }.onSuccess { setsOfCards ->
                    if (setsOfCards.isEmpty()) {
                        mutableScreenState.value = HomeContract.ScreenState.Empty
                    } else {
                        mutableScreenState.value = HomeContract.ScreenState.Success(
                            setsOfCards = setsOfCards.map { it.toUI() }
                        )
                    }
                }
            }
        }
    }

    fun event(event: HomeContractEvent) {
        when (event) {
            HomeContractEvent.OnClickDrawer -> openDrawer()
            HomeContractEvent.OnClickToolbarLanguage -> onClickToolbarLanguage()
            HomeContractEvent.OnClickShowAllCategory -> {
                viewModelScope.launch {
                    _channel.send(HomeContractChannel.NavigateToAllCategory)
                }
            }
            is HomeContractEvent.OnClickPlayCategory -> {
                viewModelScope.launch {
                    _channel.send(HomeContractChannel.NavigateToCategoryGame(event.value))
                }
            }
            is HomeContractEvent.OnClickCategory -> {
                viewModelScope.launch {
                    _channel.send(HomeContractChannel.NavigateToCategory(event.value))
                }
            }
            is HomeContractEvent.OnClickBookmarkCategory -> {
                mutableScreenState.update { state ->
                    (state as? HomeContract.ScreenState.Success)?.let {
                        state.copy(
                            setsOfCards = state.setsOfCards.map {
                                if (it.id == event.value.id) it.copy(
                                    isBookmarked = !event.value.isBookmarked
                                ) else it
                            }
                        )
                    } ?: state
                }
            }
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
sealed interface HomeContract {

    @Immutable
    data class ToolbarState(
        val userName: String? = null,
        val learningLanguage: LanguageUI? = null
    ) : HomeContract

    @Stable
    sealed interface ScreenState : HomeContract {
        @Immutable
        object Loading : ScreenState

        @Immutable
        object Empty : ScreenState

        @Immutable
        data class Success(
            val setsOfCards: List<CategoryUI>
        ) : ScreenState
    }
}

sealed interface HomeContractEvent {
    object OnClickDrawer : HomeContractEvent
    object OnClickToolbarLanguage : HomeContractEvent
    object OnClickShowAllCategory : HomeContractEvent
    data class OnClickPlayCategory(val value: CategoryUI) : HomeContractEvent
    data class OnClickBookmarkCategory(val value: CategoryUI) : HomeContractEvent
    data class OnClickCategory(val value: CategoryUI) : HomeContractEvent
}

sealed interface HomeContractChannel {
    object OpenDrawer : HomeContractChannel
    object NavigateToLanguageSelectPage : HomeContractChannel
    object NavigateToAllCategory : HomeContractChannel
    data class NavigateToCategoryGame(val value: CategoryUI) : HomeContractChannel
    data class NavigateToCategory(val value: CategoryUI) : HomeContractChannel
    data class ShowMessage(val messageContent: MessageContent) : HomeContractChannel
}