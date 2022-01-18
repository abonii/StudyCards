package abm.co.studycards.di

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.pref.Prefs.Companion.SHARED_PREFERENCES
import abm.co.studycards.data.pref.PrefsImpl
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class PrefsModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePrefs(sharedPreferences: SharedPreferences): Prefs {
        return PrefsImpl(sharedPreferences)
    }


}