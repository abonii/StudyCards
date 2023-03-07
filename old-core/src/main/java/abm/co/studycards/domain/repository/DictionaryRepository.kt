package abm.co.studycards.domain.repository

import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.OxfordResult

interface DictionaryRepository {
    suspend fun getOxfordWord(
        word: String,
        sourceLang: String,
        targetLang: String,
        oxfordApiId: String,
        oxfordApiKey: String
    ): ResultWrapper<OxfordResult>

    suspend fun getYandexWord(
        word: String,
        sourceLang: String,
        targetLang: String,
        yandexApiKey: String
    ): ResultWrapper<String>
}