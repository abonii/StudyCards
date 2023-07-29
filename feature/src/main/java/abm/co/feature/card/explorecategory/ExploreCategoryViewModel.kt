package abm.co.feature.card.explorecategory

import abm.co.designsystem.base.SelectionHolder
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.model.explore.ExploreCategoryContext
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.RedesignServerRepository
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.card.model.toUI
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.lanugage.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreCategoryViewModel @Inject constructor(
    private val serverRepository: RedesignServerRepository,
    private val languagesRepository: LanguagesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryUI: CategoryUI = savedStateHandle["category"]
        ?: throw NullPointerException("Category must be not null")

    private val _channel = Channel<ExploreCategoryContractChannel>()
    val channel: Flow<ExploreCategoryContractChannel> = _channel.receiveAsFlow()

    val nativeLanguage = languagesRepository.getNativeLanguage().map { it?.toUI() }
    val learningLanguage = languagesRepository.getLearningLanguage().map { it?.toUI() }

    private val _state: MutableStateFlow<ExploreCategoryContractState> =
        MutableStateFlow(
            ExploreCategoryContractState(
                categoryTitle = categoryUI.title,
                image = categoryUI.imageURL,
                fromLang = nativeLanguage,
                toLang = learningLanguage
            )
        )
    val state: StateFlow<ExploreCategoryContractState> = _state.asStateFlow()

    private val cards = mutableStateListOf<SelectionHolder<CardUI>>()

    init {
        fetchExploreCategory()
    }

    fun onEvent(event: ExploreCategoryContractEvent) {
        when (event) {
            is ExploreCategoryContractEvent.Success.OnClickAddCard -> {
                cards.indexOfFirst {
                    it.item.cardID == event.item.item.cardID
                }.takeIf {
                    it != -1
                }?.let {
                    cards[it] = cards[it].copy(isSelected = !event.item.isSelected)
                }
            }

            ExploreCategoryContractEvent.OnClickCategoryShare -> {
                viewModelScope.launch {
                    _channel.send(ExploreCategoryContractChannel.Share)
                }
            }

            ExploreCategoryContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(ExploreCategoryContractChannel.NavigateBack)
                }
            }

            ExploreCategoryContractEvent.Success.OnClickAddPrimaryButton -> {
                viewModelScope.launch {
                    serverRepository.addExploreCategoryToUserCategory(
                        request = ExploreCategoryContext(
                            category = categoryUI.toDomain(),
                            cards = cards.filter { it.isSelected }.map { it.item.toDomain() }
                        )
                    )
                }
            }
        }
    }

    private fun fetchExploreCategory() {
        viewModelScope.launch {
            serverRepository.getExploreCategory(categoryUI.id)
                .onSuccess { categoryResponse ->
                    cards.clear()
                    cards.addAll(categoryResponse.cards.map { SelectionHolder(it.toUI()) })
                    _state.update { oldState ->
                        oldState.copy(
                            state = ExploreCategoryContractState.State.Success(
                                categoryUI = categoryResponse.category.toUI(),
                                cards = cards,
                                image = categoryUI.imageURL
                            )
                        )
                    }
                }
                .onFailure {
                    it.sendException()
                }
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent().let {
                _channel.send(ExploreCategoryContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
data class ExploreCategoryContractState(
    val categoryTitle: String,
    val image: String?,
    val fromLang: Flow<LanguageUI?>,
    val toLang: Flow<LanguageUI?>,
    val state: State = State.Loading
) {
    sealed interface State {
        object Loading : State
        object Empty : State
        data class Success(
            val image: String?,
            val categoryUI: CategoryUI,
            val cards: SnapshotStateList<SelectionHolder<CardUI>>
        ) : State {

            sealed interface SelectedCards {
                object All : SelectedCards
                data class Some(val count: Int) : SelectedCards
            }

            val selectedCardsCount = derivedStateOf {
                val selectedCount = cards.count { it.isSelected }
                return@derivedStateOf if (selectedCount == cards.count()) {
                    SelectedCards.All
                } else SelectedCards.Some(selectedCount)
            }
        }
    }
}

@Immutable
sealed interface ExploreCategoryContractEvent {
    object OnClickCategoryShare : ExploreCategoryContractEvent
    object OnBack : ExploreCategoryContractEvent

    sealed interface Success : ExploreCategoryContractEvent {
        data class OnClickAddCard(val item: SelectionHolder<CardUI>) : Success
        object OnClickAddPrimaryButton : Success
    }
}

@Immutable
sealed interface ExploreCategoryContractChannel {

    object NavigateBack : ExploreCategoryContractChannel

    object Share : ExploreCategoryContractChannel

    data class ShowMessage(
        val messageContent: MessageContent
    ) : ExploreCategoryContractChannel
}
