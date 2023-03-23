package abm.co.feature.card.model

import androidx.compose.runtime.Immutable

@Immutable
data class CardItemUI(
    val name: String,
    val learnOrKnown: CardKindUI,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val cardID: String
)