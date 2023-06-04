package abm.co.data.repository

import abm.co.data.local.dao.LibraryDao
import abm.co.data.model.library.toDTO
import abm.co.data.model.library.toDomain
import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import abm.co.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val dao: LibraryDao
): LibraryRepository {
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
        return dao.getBook(title)?.toDomain()
    }

    override suspend fun getChapters(bookUrl: String): List<ChapterEntity> {
        return dao.getChapters(bookUrl).map { it.toDomain() }
    }

    override suspend fun getImages(bookUrl: String): List<ImageEntity> {
        return dao.getImages(bookUrl).map { it.toDomain() }
    }
}