package abm.co.domain.model

data class Card(
    val name: String,
    val translation: String,
    val imageUrl: String,
    val example: String,
    val kind: CardKind,
    val learnedPercent: Float, // 0..1
    val categoryID: String,
    val repeatedCount: Int,
    val nextRepeatTime: Long,
    val id: String
) {
    companion object {
        const val kind = "kind"
    }
}
