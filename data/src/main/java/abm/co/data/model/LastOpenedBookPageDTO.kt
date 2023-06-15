package abm.co.data.model

import abm.co.domain.model.LastOpenedBookPage
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class LastOpenedBookPageDTO(
    @PrimaryKey val bookUrl: String,
    val chapterUrl: String,
    val page: Int // starts from chapter
)

fun LastOpenedBookPage.toDTO() = LastOpenedBookPageDTO(
    bookUrl = bookUrl,
    chapterUrl = chapterUrl,
    page = page
)

fun LastOpenedBookPageDTO.toDomain() = LastOpenedBookPage(
    bookUrl = bookUrl,
    chapterUrl = chapterUrl,
    page = page
)
