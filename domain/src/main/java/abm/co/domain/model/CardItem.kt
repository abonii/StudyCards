package abm.co.domain.model

data class CardItem(
    val name: String,
    val learnOrKnown: LearnOrKnown,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val cardID: String
)