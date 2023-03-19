package abm.co.domain.model

data class SetOfCards(
    val name: String,
    val cardsCount: Int,
    val isBookmarked: Boolean,
    val creatorName: String?,
    val creatorID: String?,
    val imageURL: String?,
    val id: String
)
