package abm.co.feature.card.model

data class CardUI(
    val name: String,
    val translations: String,
    val imageUrl: String,
    val examples: String,
    val learnOrKnown: LearnOrKnownUI,
    val categoryID: String,
    val repeatCount: Int,
    val nextRepeatTime: Long,
    val cardID: String
)
