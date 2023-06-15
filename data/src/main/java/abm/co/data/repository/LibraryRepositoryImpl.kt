package abm.co.data.repository

import abm.co.data.local.dao.LibraryDao
import abm.co.data.model.library.toDTO
import abm.co.data.model.library.toDomain
import abm.co.data.model.toDTO
import abm.co.data.model.toDomain
import abm.co.domain.model.LastOpenedBookPage
import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import abm.co.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val dao: LibraryDao
) : LibraryRepository {
    override suspend fun insertBook(bookEntity: BookEntity) {
        withContext(Dispatchers.IO) {
            dao.insertBooks(bookEntity.toDTO())
        }
    }

    override suspend fun insertImages(images: List<ImageEntity>) {
        withContext(Dispatchers.IO) {
            dao.insertImages(images.map { it.toDTO() })
        }
    }

    override suspend fun insertChapters(chapterEntities: List<ChapterEntity>) {
        withContext(Dispatchers.IO) {
            dao.insertChapters(chapterEntities.map { it.toDTO() })
        }
    }

    override suspend fun getBook(title: String): BookEntity? {
        return withContext(Dispatchers.IO) {
            dao.getBook(title)?.toDomain()
        }
    }

    override suspend fun getChapters(bookUrl: String): List<ChapterEntity> {
        return withContext(Dispatchers.IO) {
            dao.getChapters(bookUrl).map { it.toDomain() }
        }
    }

    override suspend fun getImages(bookUrl: String): List<ImageEntity> {
        return withContext(Dispatchers.IO) {
            dao.getImages(bookUrl).map { it.toDomain() }
        }
    }

    override suspend fun getLastOpenedBookPage(bookUrl: String): LastOpenedBookPage? {
        return withContext(Dispatchers.IO) {
            dao.getLastOpenedBookPage(bookUrl)?.toDomain()
        }
    }

    override suspend fun setLastOpenedBookPage(item: LastOpenedBookPage) {
        withContext(Dispatchers.IO) {
            dao.insertLastOpenedBookPage(item.toDTO())
        }
    }
}