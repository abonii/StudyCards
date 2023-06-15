package abm.co.feature.book.utils

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun unzipEpubFile(bookTitle: String, epubFile: File): Either<Failure, Triple<BookEntity, List<ChapterEntity>, List<ImageEntity>>> {
    return withContext(Dispatchers.IO) {
        try {
            val zipInputStream = epubFile.inputStream()
            val epub = zipInputStream.use { createEpubBookWithSiegman(it) }
            val epubBook = epub.toBook().copy(
                title = bookTitle
            )
            val chapters = epub.chapters.map { it.toChapter(epubBook.url) }
            val images = epub.images.map { it.toEncodedImage(epubBook.url) }
            epubFile.delete()
            Either.Right(
                Triple(
                    epubBook,
                    chapters,
                    images
                )
            )
        } catch (e: Exception) {
            Either.Left(e.mapToFailure())
        }
    }
}