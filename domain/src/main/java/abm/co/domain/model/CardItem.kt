package abm.co.domain.model

data class CardItem(
    val name: String,
    val kind: CardKind,
    val translation: String,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val id: String
)