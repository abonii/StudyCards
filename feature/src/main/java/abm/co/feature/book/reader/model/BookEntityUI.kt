package abm.co.feature.book.reader.model

import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import androidx.compose.runtime.Immutable

@Immutable
data class BookEntityUI(
    val url: String,
    val title: String,
    val chaptersName: List<String>?,
    val coverImageUrl: String,
    val imagesPath: List<String>?
)

@Immutable
data class ImageEntityUI(
    val bookUrl: String,
    val path: String,
    val encodedImage: String,
    val id: Int = 0
)

@Immutable
data class ChapterEntityUI(
    val url: String,
    val bookUrl: String,
    val title: String?,
    val body: String
)


fun BookEntity.toUI() = BookEntityUI(
    url = url,
    title = title,
    chaptersName = chaptersName,
    coverImageUrl = coverImageUrl,
    imagesPath = imagesPath
)

fun ChapterEntity.toUI() = ChapterEntityUI(
    url = url,
    bookUrl = bookUrl,
    title = title,
    body = body
)

fun ImageEntity.toUI() = ImageEntityUI(
    bookUrl = bookUrl,
    path = path,
    encodedImage = encodedImage
)
