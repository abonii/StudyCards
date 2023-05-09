package abm.co.feature.card.main

import abm.co.designsystem.functional.safeLet
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val serverRepository: ServerRepository,
) : ViewModel() {

    private val _channel = Channel<MainCardContractChannel>()
    val channel: Flow<MainCardContractChannel> = _channel.receiveAsFlow()

    private val _state: MutableStateFlow<MainCardContractState> =
        MutableStateFlow(MainCardContractState.Loading)
    val state: StateFlow<MainCardContractState> = _state.asStateFlow()

    init {
        fetchCategories()
    }

    fun onEvent(event: MainCardContractEvent) {
        when (event) {
            is MainCardContractEvent.OnClickCategory -> {
                viewModelScope.launch {
//                    _channel.send() todo
                }
            }

            MainCardContractEvent.OnClickCategoryConfirmClose -> {
                _state.update { oldState ->
                    (oldState as? MainCardContractState.Success)?.copy(
                        categoryConfirmShare = null
                    ) ?: oldState
                }
            }

            is MainCardContractEvent.OnClickCategoryConfirmShare -> {
                viewModelScope.launch {
                    if(event.item.published == true){
                        serverRepository.removeCategory(event.item.id)
                    } else {
                        serverRepository.copyUserCategoryToExploreCollection(event.item.id)
                            .onFailure {
                                it.sendException()
                            }
                            .onSuccess {
                                onEvent(MainCardContractEvent.OnClickCategoryConfirmClose)
                            }
                    }
                }
            }

            is MainCardContractEvent.OnClickCategoryPlay -> {
                viewModelScope.launch {
                    serverRepository.copyExploreCategoryToUserCollection(event.item.id)
                        .onFailure {
                            it.sendException()
                        }
                        .onSuccess {
                            _channel.send(
                                MainCardContractChannel.NavigateToLearnGame(event.item)
                            )
                        }
                }
            }

            is MainCardContractEvent.OnClickCategoryShare -> {
                _state.update { oldState ->
                    (oldState as? MainCardContractState.Success)?.copy(
                        categoryConfirmShare = event.item
                    ) ?: oldState
                }
            }
        }
    }

    private fun fetchCategories() {
        combine(
            serverRepository.getCategories,
            serverRepository.getUserCategories
        ) { categoriesEither, userCategoriesEither ->
            safeLet(
                categoriesEither.asRight?.b,
                userCategoriesEither.asRight?.b
            ) { categories, userCategories ->
                if (categories.isEmpty() && userCategories.isEmpty()) {
                    _state.value = MainCardContractState.Empty
                } else {
                    _state.value = MainCardContractState.Success(
                        ourCategories = categories
                            .filterNot { it.creatorID == firebaseAuth.currentUser?.uid }
                            .map { it.toUI() },
                        userCategories = userCategories
                            .map { category -> category.toUI(published = categories.find { it.id == category.id } != null) },
                        categoryConfirmShare = null
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(MainCardContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
sealed interface MainCardContractState {
    object Loading : MainCardContractState
    object Empty : MainCardContractState
    data class Success(
        val ourCategories: List<CategoryUI>,
        val userCategories: List<CategoryUI>,
        val categoryConfirmShare: CategoryUI?
    ) : MainCardContractState
}

@Immutable
sealed interface MainCardContractEvent {
    data class OnClickCategory(val item: CategoryUI) : MainCardContractEvent
    data class OnClickCategoryPlay(val item: CategoryUI) : MainCardContractEvent
    data class OnClickCategoryShare(val item: CategoryUI) : MainCardContractEvent
    data class OnClickCategoryConfirmShare(val item: CategoryUI) : MainCardContractEvent
    object OnClickCategoryConfirmClose : MainCardContractEvent
}

@Immutable
sealed interface MainCardContractChannel {

    data class NavigateToLearnGame(
        val item: CategoryUI
    ) : MainCardContractChannel

    data class ShowMessage(
        val messageContent: MessageContent
    ) : MainCardContractChannel
}
