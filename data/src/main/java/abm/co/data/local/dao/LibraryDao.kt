package abm.co.data.local.dao

import abm.co.data.model.library.BookEntityDTO
import abm.co.data.model.library.ChapterEntityDTO
import abm.co.data.model.library.ImageEntityDTO
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Query("SELECT * FROM BookEntityDTO")
    fun getBooks(): Flow<List<BookEntityDTO>>

    @Query("SELECT * FROM ImageEntityDTO where bookUrl = :bookUrl")
    suspend fun getImages(bookUrl: String): List<ImageEntityDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(bookEntity: BookEntityDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(bookEntity: List<BookEntityDTO>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntityDTO>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(images: List<ChapterEntityDTO>)

    @Query("SELECT * FROM ChapterEntityDTO WHERE bookUrl = :bookUrl")
    suspend fun getChapters(bookUrl: String): List<ChapterEntityDTO>

    @Delete
    suspend fun remove(bookEntity: BookEntityDTO)

    @Query("DELETE FROM BookEntityDTO WHERE url = :bookUrl")
    suspend fun remove(bookUrl: String)

    @Update
    suspend fun update(bookEntity: BookEntityDTO)

    @Query("SELECT * FROM BookEntityDTO WHERE title = :title")
    suspend fun getBook(title: String): BookEntityDTO?
}
