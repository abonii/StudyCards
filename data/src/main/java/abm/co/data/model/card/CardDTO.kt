package abm.co.data.model.card

import androidx.annotation.Keep

@Keep
data class CardDTO(
    val name: String,
    val translations: String,
    val imageUrl: String,
    val examples: String,
    val learnOrKnown: LearnOrKnownDTO,
    val categoryID: String,
    val repeatCount: Int,
    val nextRepeatTime: Long,
    val cardID: String
)
