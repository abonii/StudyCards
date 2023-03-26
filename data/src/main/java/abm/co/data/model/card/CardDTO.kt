package abm.co.data.model.card

import abm.co.domain.model.Card
import androidx.annotation.Keep

@Keep
data class CardDTO(
    val name: String,
    val translations: String,
    val imageUrl: String,
    val examples: String,
    val kind: CardKindDTO,
    val categoryID: String,
    val repeatCount: Int,
    val nextRepeatTime: Long,
    val id: String
)

fun Card.toDTO() = CardDTO(
    name = name,
    translations = translations,
    imageUrl = imageUrl,
    examples = examples,
    kind = kind.toDTO(),
    categoryID = categoryID,
    repeatCount = repeatCount,
    nextRepeatTime = nextRepeatTime,
    id = id
)
