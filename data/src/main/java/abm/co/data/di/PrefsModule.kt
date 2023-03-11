package abm.co.data.di

import abm.co.data.pref.PrefsImpl
import abm.co.domain.prefs.Prefs
import abm.co.domain.prefs.Prefs.Companion.SHARED_PREFERENCES
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
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


    @Provides
    @Singleton
    fun provideNonNullGson(): Gson = GsonBuilder().run {
        setLenient()
        create()
    }

}