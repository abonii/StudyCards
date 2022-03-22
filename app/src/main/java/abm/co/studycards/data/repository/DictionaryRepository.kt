package abm.co.studycards.data.repository

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.oxford.RetrieveEntry
import abm.co.studycards.data.model.yandex.TranslatedText

interface DictionaryRepository {
    suspend fun getOxfordWord(word: String, sl: String, tl: String): ResultWrapper<RetrieveEntry>
    suspend fun getYandexWord(word: String, sl: String, tl: String): ResultWrapper<TranslatedText>
}