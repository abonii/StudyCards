package abm.co.domain.model

data class CardItem(
    val name: String,
    val kind: CardKind,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val cardID: String
)