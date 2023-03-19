package abm.co.data.datastore

import abm.co.data.model.user.LanguageDTO
import android.content.Context
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class LanguagesDataStore @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    serializer: LanguagesSerializer
) {
    companion object {
        private const val LEARNING_LANGUAGE_DATA_STORE = "learning_languages_data_store.json"
        private const val NATIVE_LANGUAGES_DATA_STORE = "native_languages_data_store.json"
    }

    private val Context.nativeLanguagesDataStore by dataStore(
        fileName = NATIVE_LANGUAGES_DATA_STORE,
        serializer = serializer
    )

    private val Context.learningLanguagesDataStore by dataStore(
        fileName = LEARNING_LANGUAGE_DATA_STORE,
        serializer = serializer
    )

    fun getNativeLanguage(): Flow<LanguageDTO?> = applicationContext.nativeLanguagesDataStore.data

    fun getLearningLanguage(): Flow<LanguageDTO?> = applicationContext.nativeLanguagesDataStore.data

    suspend fun setNativeLanguage(value: LanguageDTO?) {
        applicationContext.nativeLanguagesDataStore.updateData {
            value
        }
    }

    suspend fun setLearningLanguage(value: LanguageDTO?) {
        applicationContext.learningLanguagesDataStore.updateData {
            value
        }
    }
}
