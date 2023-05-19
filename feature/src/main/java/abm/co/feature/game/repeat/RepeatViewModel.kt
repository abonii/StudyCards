package abm.co.feature.game.repeat

import abm.co.data.di.ApplicationScope
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.game.model.GameKindUI
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RepeatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val serverRepository: ServerRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _cards: Array<CardUI> = savedStateHandle["cards"]
        ?: throw RuntimeException("cannot be empty CARDS argument")

    val cards = ArrayList(_cards.toList().shuffled())

    var currentGame = MutableStateFlow<GameKindUI?>(GameKindUI.Review)

    fun takeCards(): Array<CardUI> {
        return cards.take(5).toTypedArray()
    }

    fun updateLearnedCards() {
        applicationScope.launch {
            currentGame.value = null
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
}