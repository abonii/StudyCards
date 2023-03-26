package abm.co.feature.card.model

import abm.co.domain.model.CardItem
import androidx.compose.runtime.Immutable

@Immutable
data class CardItemUI(
    val name: String,
    val translation: String,
    val kind: CardKindUI,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val id: String
)

fun CardItem.toUI() = CardItemUI(
    name = name,
    translation = translation,
    kind = kind.toUI(),
    categoryID = categoryID,
    learnedPercent = learnedPercent,
    nextRepeatTime = nextRepeatTime,
    id = id
)