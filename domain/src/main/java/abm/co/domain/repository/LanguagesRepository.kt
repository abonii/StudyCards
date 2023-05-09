package abm.co.domain.repository

import abm.co.domain.model.Language
import kotlinx.coroutines.flow.Flow

interface LanguagesRepository {

    fun getNativeLanguage(): Flow<Language?>

    fun getLearningLanguage(): Flow<Language?>

    suspend fun setNativeLanguage(language: Language)

    suspend fun setLearningLanguage(language: Language)

    suspend fun setAppLanguage(language: Language)

    fun getAppLanguage(): Language?
}
