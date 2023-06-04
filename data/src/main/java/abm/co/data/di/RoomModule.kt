package abm.co.data.di

import abm.co.data.local.AppDatabase
import abm.co.data.local.AppDatabaseOperations
import abm.co.data.repository.LibraryRepositoryImpl
import abm.co.domain.repository.LibraryRepository
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.createRoom(context, "libraryEntry")
    }

    @Provides
    @Singleton
    fun provideRepository(database: AppDatabase): LibraryRepository {
        return LibraryRepositoryImpl(database.libraryDao())
    }

    @Provides
    @Singleton
    fun provideAppDatabaseOperations(database: AppDatabase): AppDatabaseOperations {
        return database
    }
}
