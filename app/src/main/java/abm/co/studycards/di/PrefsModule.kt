package abm.co.studycards.di

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.pref.Prefs.Companion.SHARED_PREFERENCES
import abm.co.studycards.data.pref.PrefsImpl
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class PrefsModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    fun providePrefs(prefsImpl: PrefsImpl): Prefs = prefsImpl

}