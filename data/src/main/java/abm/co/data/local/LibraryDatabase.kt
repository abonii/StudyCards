package abm.co.data.local

import abm.co.data.local.converter.DataConverter
import abm.co.data.local.dao.LibraryDao
import abm.co.data.model.LastOpenedBookPageDTO
import abm.co.data.model.library.BookEntityDTO
import abm.co.data.model.library.ChapterEntityDTO
import abm.co.data.model.library.ImageEntityDTO
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase


interface AppDatabaseOperations {
    /**
     * Execute the whole database calls as an atomic operation
     */
    suspend fun <T> transaction(block: suspend () -> T): T
}

@Database(
    entities = [
        BookEntityDTO::class,
        ImageEntityDTO::class,
        ChapterEntityDTO::class,
        LastOpenedBookPageDTO::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase(), AppDatabaseOperations {
    abstract fun libraryDao(): LibraryDao

    override suspend fun <T> transaction(block: suspend () -> T): T = withTransaction(block)

    companion object {
        fun createRoom(ctx: Context, name: String) = Room
            .databaseBuilder(ctx, AppDatabase::class.java, name)
            .fallbackToDestructiveMigration()
            .build()
    }
}