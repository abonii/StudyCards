package abm.co.data.model.card

import abm.co.domain.model.Card
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CardDTO(
    val id: String = "",
    val name: String = "",
    val kind: CardKindDTO = CardKindDTO.UNDEFINED,
    val example: String = "",
    val translation: String = "",
    val imageUrl: String = "",
    val learnedPercent: Float = 0f, // 0..1
    val categoryID: String = "",
    val repeatedCount: Int = 0,
    val nextRepeatTime: Long = 0L
)

fun Card.toDTO() = CardDTO(
    name = name,
    translation = translation,
    imageUrl = imageUrl,
    example = example,
    kind = kind.toDTO(),
    categoryID = categoryID,
    repeatedCount = repeatedCount,
    nextRepeatTime = nextRepeatTime,
    id = id
)

fun CardDTO.toDomain() = Card(
    name = name,
    translation = translation,
    imageUrl = imageUrl,
    example = example,
    kind = kind.toDomain(),
    categoryID = categoryID,
    repeatedCount = repeatedCount,
    nextRepeatTime = nextRepeatTime,
    id = id,
    learnedPercent = learnedPercent
)
