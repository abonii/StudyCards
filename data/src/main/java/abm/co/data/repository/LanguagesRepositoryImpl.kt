package abm.co.data.repository

import abm.co.data.datastore.LanguagesDataStore
import abm.co.data.model.user.toDTO
import abm.co.data.model.user.toDomain
import abm.co.domain.model.Language
import abm.co.domain.prefs.Prefs
import abm.co.domain.repository.LanguagesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class LanguagesRepositoryImpl @Inject constructor(
    private val languagesDataStore: LanguagesDataStore,
    private val prefs: Prefs
): LanguagesRepository {

    override fun getNativeLanguage(): Flow<Language?> {
        return languagesDataStore.getNativeLanguage().map {
            it?.toDomain()
        }
    }

    override fun getLearningLanguage(): Flow<Language?> {
        return languagesDataStore.getLearningLanguage().map {
            it?.toDomain()
        }
    }

    override suspend fun setNativeLanguage(language: Language) {
        languagesDataStore.setNativeLanguage(language.toDTO())
    }

    override suspend fun setLearningLanguage(language: Language) {
        languagesDataStore.setLearningLanguage(language.toDTO())
    }

    override suspend fun setAppLanguage(language: Language) {
        prefs.setAppLanguage(language)
    }

    override fun getAppLanguage(): Language? {
        return prefs.getAppLanguage()
    }
}
