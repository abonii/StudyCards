package abm.co.domain.model.library

data class BookEntity(
    val url: String,
    val title: String,
    val chaptersName: List<String>?,
    val coverImageUrl: String,
    val imagesPath: List<String>?
)

data class ImageEntity(
    val bookUrl: String,
    val path: String,
    val encodedImage: String,
    val id: Int = 0
)

data class ChapterEntity(
    val url: String,
    val bookUrl: String,
    val title: String?,
    val body: String
)