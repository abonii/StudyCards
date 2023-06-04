package abm.co.data.model.library

import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class BookEntityDTO(
    @PrimaryKey val url: String,
    val title: String,
    val chaptersName: List<String>?,
    val coverImageUrl: String,
    val imagesPath: List<String>?
)

@Keep
@Entity
data class ImageEntityDTO(
    val bookUrl: String,
    val path: String,
    val encodedImage: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

@Keep
@Entity
data class ChapterEntityDTO(
    @PrimaryKey val url: String,
    val bookUrl: String,
    val title: String?,
    val body: String
)

fun BookEntity.toDTO() = BookEntityDTO(
    url = url,
    title = title,
    chaptersName = chaptersName,
    coverImageUrl = coverImageUrl,
    imagesPath = imagesPath
)

fun ChapterEntity.toDTO() = ChapterEntityDTO(
    url = url,
    bookUrl = bookUrl,
    title = title,
    body = body
)

fun ImageEntity.toDTO() = ImageEntityDTO(
    bookUrl = bookUrl,
    path = path,
    encodedImage = encodedImage
)

fun BookEntityDTO.toDomain() = BookEntity(
    url = url,
    title = title,
    chaptersName = chaptersName,
    coverImageUrl = coverImageUrl,
    imagesPath = imagesPath
)

fun ChapterEntityDTO.toDomain() = ChapterEntity(
    url = url,
    bookUrl = bookUrl,
    title = title,
    body = body
)

fun ImageEntityDTO.toDomain() = ImageEntity(
    bookUrl = bookUrl,
    path = path,
    encodedImage = encodedImage
)
