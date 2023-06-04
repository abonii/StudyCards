package abm.co.domain.model.library

data class Book(
    val id: String,
    val name: String,
    val description: String,
    val languageCode: String,
    val level: Level,
    val image: String,
    val bannerImage: String,
    val kind: String,
    val link: String,
    val visible: Boolean
) {
    enum class Level {
        A1, A2, B1, B2, C1, C2;
    }
}