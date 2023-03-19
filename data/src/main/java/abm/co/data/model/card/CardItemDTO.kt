package abm.co.data.model.card

import androidx.annotation.Keep

@Keep
data class CardItemDTO(
    val name: String,
    val learnOrKnown: LearnOrKnownDTO,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val cardID: String
)