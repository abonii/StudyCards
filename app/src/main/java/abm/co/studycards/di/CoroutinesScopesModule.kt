package abm.co.studycards.di

import abm.co.studycards.util.Constants.TAG_ERROR
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {

    @Singleton
    @Provides
    fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            Log.e(TAG_ERROR, "provideCoroutineExceptionHandler: ${exception.message}")
        }
    }

    @Singleton
    @Provides
    fun providesCoroutineScope(coroutineHandler: CoroutineExceptionHandler): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO + coroutineHandler)
    }
}