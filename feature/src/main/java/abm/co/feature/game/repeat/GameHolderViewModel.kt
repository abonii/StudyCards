package abm.co.feature.game.repeat

import abm.co.data.di.ApplicationScope
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.game.model.GameKindUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.util.lerp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class GameHolderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val serverRepository: ServerRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _cards: Array<CardUI> = savedStateHandle["cards"]
        ?: throw RuntimeException("cannot be empty CARDS argument")

    private var viewedTimes = 0

    val gameKind: GameKindUI? = savedStateHandle["game_kind"]

    private val cards = ArrayList(_cards.toList().shuffled())

    private val _uiState = MutableStateFlow(RepeatContract.ScreenState())
    var uiState = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(RepeatContract.DialogState())
    var dialogState = _dialogState.asStateFlow()

    private val _channel = Channel<RepeatContract.Channel>()
    var channel = _channel.receiveAsFlow()

    fun takeCards(): Array<CardUI> {
        return if(gameKind == GameKindUI.Review) {
            cards.toTypedArray()
        } else {
            cards.take(5).toTypedArray()
        }
    }

    fun updateLearnedCards() {
        applicationScope.launch {
            takeCards().forEach { card ->
                serverRepository.updateUserCard(
                    card.copy(
                        learnedPercent = try {
                            (card.repeatedCount + 1).toFloat() / 5
                        } catch (e: Exception) {
                            0f
                        },
                        repeatedCount = card.repeatedCount + 1,
                        nextRepeatTime = Calendar.getInstance().apply {
                            when (card.repeatedCount) {
                                0 -> {
                                    add(Calendar.HOUR_OF_DAY, 12)
                                }

                                1 -> {
                                    add(Calendar.DAY_OF_MONTH, 1)
                                }

                                2 -> {
                                    add(Calendar.DAY_OF_MONTH, 2)
                                }

                                else -> add(Calendar.DAY_OF_MONTH, 3)
                            }
                        }.timeInMillis
                    ).toDomain()
                )
            }
        }
    }

    fun updateProgress(
        newValue: Float = 0f,
        currentGame: GameKindUI? = uiState.value.currentGame
    ) {
        _uiState.update { oldState ->
            val newProgress = currentGame?.let {
                val currentGameProgress = when (currentGame) {
                    GameKindUI.Review -> 0.1f
                    GameKindUI.Pair -> 0.33f
                    GameKindUI.Guess -> 0.66f
                }
                val nextGameProgress = when (currentGame) {
                    GameKindUI.Review -> 0.33f
                    GameKindUI.Pair -> 0.66f
                    GameKindUI.Guess -> 1f
                }
                lerp(
                    currentGameProgress,
                    nextGameProgress,
                    newValue
                )
            } ?: lerp(
                0f,
                1f,
                try {
                    ((newValue * takeCards().size + viewedTimes) / _cards.size).coerceAtMost(0.99f)
                } catch (e: Exception) {
                    0f
                }
            )
            oldState.copy(
                progress = newProgress
            )
        }
    }

    fun updateCurrentGame(kind: GameKindUI) {
        _uiState.update {
            it.copy(currentGame = kind)
        }
    }

    fun dismissDialog() {
        _dialogState.value = RepeatContract.DialogState()
    }

    fun showConfirmDialogToNavigateBack() {
        _dialogState.value = RepeatContract.DialogState(
            backPressConfirm = true
        )
    }

    fun showGameFinished() {
        _dialogState.value = RepeatContract.DialogState(
            finishedGame = gameKind
        )
    }

    fun showContinueToRepeatOrFinishRepeating() {
        if (cards.size > 5) {
            _dialogState.value = RepeatContract.DialogState(
                continueRepeatOrFinish = true
            )
        } else {
            _dialogState.value = RepeatContract.DialogState(
                finishedRepeat = true
            )
        }
    }

    fun removePlayedCards(): Boolean {
        repeat(takeCards().size) {
            cards.removeAt(0)
            viewedTimes++
        }
        return cards.size > 0
    }

    fun onConfirmContinue() {
        viewModelScope.launch {
            _dialogState.value = RepeatContract.DialogState()
            removePlayedCards()
            _channel.send(
                RepeatContract.Channel.StartRepeat(takeCards())
            )
        }
    }
}

@Stable
sealed interface RepeatContract {

    data class ScreenState(
        val currentGame: GameKindUI = GameKindUI.Review,
        val progress: Float = 0.01f // 0..1
    ) : RepeatContract

    data class DialogState(
        val backPressConfirm: Boolean = false,
        val continueRepeatOrFinish: Boolean = false,
        val finishedRepeat: Boolean = false,
        val finishedGame: GameKindUI? = null
    ) : RepeatContract

    @Immutable
    sealed interface Channel {
        class StartRepeat(val cards: Array<CardUI>) : Channel
    }
}
