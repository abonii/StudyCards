package abm.co.domain.repository

import abm.co.domain.model.LastOpenedBookPage
import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    suspend fun insertBook(bookEntity: BookEntity)

    suspend fun insertImages(images: List<ImageEntity>)

    suspend fun insertChapters(chapterEntities: List<ChapterEntity>)

    suspend fun getChapters(bookUrl: String): List<ChapterEntity>

    suspend fun getImages(bookUrl: String): List<ImageEntity>

    suspend fun getBook(title: String): BookEntity?

    suspend fun getLastOpenedBookPage(bookUrl: String): LastOpenedBookPage?

    suspend fun setLastOpenedBookPage(item: LastOpenedBookPage)
}