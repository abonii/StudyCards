package abm.co.feature.card.model

import abm.co.domain.model.Card
import androidx.compose.runtime.Immutable

@Immutable
data class CardUI(
    val name: String,
    val translation: String,
    val imageUrl: String,
    val example: String,
    val kind: CardKindUI,
    val categoryID: String,
    val repeatedCount: Int,
    val nextRepeatTime: Long,
    val cardID: String,
    val learnedPercent: Float, // 0..1
)

fun CardUI.toDomain() = Card(
    name = name,
    translation = translation,
    imageUrl = imageUrl,
    example = example,
    kind = kind.toDomain(),
    categoryID = categoryID,
    repeatedCount = repeatedCount,
    nextRepeatTime = nextRepeatTime,
    id = cardID,
    learnedPercent = learnedPercent
)

fun Card.toUI() = CardUI(
    name = name,
    translation = translation,
    imageUrl = imageUrl,
    example = example,
    kind = kind.toUI(),
    categoryID = categoryID,
    repeatedCount = repeatedCount,
    nextRepeatTime = nextRepeatTime,
    cardID = id,
    learnedPercent = learnedPercent
)
