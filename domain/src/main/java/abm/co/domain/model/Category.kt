package abm.co.domain.model

data class Category(
    val name: String,
    val cardsCount: Int,
    val bookmarked: Boolean,
    val creatorName: String?,
    val creatorID: String?,
    val imageURL: String?,
    val id: String,
) {
    companion object {
        const val bookmarked = "bookmarked"
    }
}