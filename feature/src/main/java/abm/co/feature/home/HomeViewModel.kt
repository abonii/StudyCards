package abm.co.feature.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    private val languagesRepository: LanguagesRepository
) : ViewModel() {

    private val _channel = Channel<HomeContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow<HomeContract.ScreenState>(
        HomeContract.ScreenState.Loading
    )
    val state: StateFlow<HomeContract.ScreenState> = _state.asStateFlow()

    private val mutableToolbarState = MutableStateFlow(HomeContract.ToolbarState())
    val toolbarState: StateFlow<HomeContract.ToolbarState> = mutableToolbarState.asStateFlow()

    private val categoryList = mutableStateListOf<CategoryUI>()

    init {
        fetchUser()
        fetchCategories()
    }

    private fun fetchUser() {
        serverRepository.getUser.combine(languagesRepository.getLearningLanguage()) { userEither, learningLang ->
            userEither.onFailure {
                it.sendException()
            }.onSuccess { user ->
                mutableToolbarState.value = HomeContract.ToolbarState(
                    userName = user?.name ?: user?.email,
                    learningLanguage = learningLang?.toUI()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchCategories() {
        serverRepository.getUserCategories.onEach { setsOfCardsEither ->
            setsOfCardsEither.onFailure {
                it.sendException()
            }.onSuccess { categories ->
                categoryList.clear()
                categoryList.addAll(categories.map { it.toUI() })
                if (categories.isEmpty()) {
                    _state.value = HomeContract.ScreenState.Empty
                } else {
                    _state.value = HomeContract.ScreenState.Success(
                        setsOfCards = categoryList
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: HomeContractEvent) {
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
                    _channel.send(HomeContractChannel.NavigateToGameKinds(event.value))
                }
            }

            is HomeContractEvent.OnClickCategory -> {
                viewModelScope.launch {
                    _channel.send(HomeContractChannel.NavigateToCategory(event.value))
                }
            }

            is HomeContractEvent.OnClickBookmarkCategory -> {
                updateBookmark(
                    categoryID = event.value.id,
                    bookmarked = !event.value.bookmarked
                )
            }

            is HomeContractEvent.OnLongClickCategory -> {
                _state.update { oldState ->
                    (oldState as? HomeContract.ScreenState.Success)
                        ?.copy(removingCategory = event.value) ?: oldState
                }
            }

            is HomeContractEvent.OnConfirmRemoveCategory -> {
                viewModelScope.launch {
                    onEvent(HomeContractEvent.OnDismissDialog)
                    serverRepository.removeUserCategory(event.value.id)
                        .onFailure {
                            it.sendException()
                        }
                }
            }
            HomeContractEvent.OnDismissDialog -> {
                _state.update { oldState ->
                    (oldState as? HomeContract.ScreenState.Success)
                        ?.copy(removingCategory = null) ?: oldState
                }
            }
        }
    }

    private fun updateBookmark(categoryID: String, bookmarked: Boolean) {
        viewModelScope.launch {
            serverRepository.updateCategoryBookmark(
                categoryID = categoryID,
                bookmarked = bookmarked
            )
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

@Immutable
sealed interface HomeContract {

    data class ToolbarState(
        val userName: String? = null,
        val learningLanguage: LanguageUI? = null
    ) : HomeContract

    sealed interface ScreenState : HomeContract {
        object Loading : ScreenState

        object Empty : ScreenState

        data class Success(
            val setsOfCards: List<CategoryUI>,
            val removingCategory: CategoryUI? = null
        ) : ScreenState
    }
}

@Immutable
sealed interface HomeContractEvent {
    object OnClickDrawer : HomeContractEvent
    object OnClickToolbarLanguage : HomeContractEvent
    object OnClickShowAllCategory : HomeContractEvent
    object OnDismissDialog : HomeContractEvent
    data class OnConfirmRemoveCategory(val value: CategoryUI) : HomeContractEvent
    data class OnClickPlayCategory(val value: CategoryUI) : HomeContractEvent
    data class OnClickBookmarkCategory(val value: CategoryUI) : HomeContractEvent
    data class OnLongClickCategory(val value: CategoryUI) : HomeContractEvent
    data class OnClickCategory(val value: CategoryUI) : HomeContractEvent
}

@Immutable
sealed interface HomeContractChannel {
    object OpenDrawer : HomeContractChannel
    object NavigateToLanguageSelectPage : HomeContractChannel
    object NavigateToAllCategory : HomeContractChannel
    data class NavigateToGameKinds(val value: CategoryUI) : HomeContractChannel
    data class NavigateToCategory(val category: CategoryUI) : HomeContractChannel
    data class ShowMessage(val messageContent: MessageContent) : HomeContractChannel
}