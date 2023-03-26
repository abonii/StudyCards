package abm.co.data.model.card

import abm.co.domain.model.CardItem
import androidx.annotation.Keep

@Keep
data class CardItemDTO(
    val name: String = "",
    val translation: String = "",
    val kind: CardKindDTO = CardKindDTO.UNDEFINED,
    val categoryID: String = "",
    val learnedPercent: Float = 0f, // 0..1
    val nextRepeatTime: Long = 0L,
    val id: String = ""
)

fun CardItemDTO.toDomain() = CardItem(
    name = name,
    translation = translation,
    kind = kind.toDomain(),
    categoryID = categoryID,
    learnedPercent = learnedPercent,
    nextRepeatTime = nextRepeatTime,
    id = id
)