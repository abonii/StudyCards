package abm.co.domain.model

data class Card(
    val name: String,
    val translations: String,
    val imageUrl: String,
    val examples: String,
    val learnOrKnown: LearnOrKnown,
    val categoryID: String,
    val repeatCount: Int,
    val nextRepeatTime: Long,
    val cardID: String
)
