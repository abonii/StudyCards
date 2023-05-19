package abm.co.feature.game.pairit.model

import abm.co.feature.card.model.CardUI
import androidx.compose.runtime.Immutable

@Immutable
data class PairItemUI(
    val cardID: String,
    val title: String
)

fun CardUI.toPairNativeItem() = PairItemUI(
    cardID = cardID,
    title = name
)

fun CardUI.toPairLearningItem() = PairItemUI(
    cardID = cardID,
    title = translation
)