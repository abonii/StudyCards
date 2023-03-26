package abm.co.feature.card.model

import abm.co.domain.model.Card

data class CardUI(
    val name: String,
    val translations: String,
    val imageUrl: String,
    val examples: String,
    val kind: CardKindUI,
    val categoryID: String,
    val repeatCount: Int,
    val nextRepeatTime: Long,
    val cardID: String
)

fun CardUI.toDomain() = Card(
    name = name,
    translations = translations,
    imageUrl = imageUrl,
    examples = examples,
    kind = kind.toDomain(),
    categoryID = categoryID,
    repeatCount = repeatCount,
    nextRepeatTime = nextRepeatTime,
    id = cardID
)

fun Card.toUI() = CardUI(
    name = name,
    translations = translations,
    imageUrl = imageUrl,
    examples = examples,
    kind = kind.toUI(),
    categoryID = categoryID,
    repeatCount = repeatCount,
    nextRepeatTime = nextRepeatTime,
    cardID = id
)
