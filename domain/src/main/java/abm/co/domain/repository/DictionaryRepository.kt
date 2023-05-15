package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.oxford.OxfordTranslationResponse
import abm.co.domain.model.yandex.TranslatedYandexText

interface DictionaryRepository {
    suspend fun getOxfordWord(
        word: String,
        fromNative: Boolean
    ): Either<Failure, OxfordTranslationResponse>

    suspend fun getYandexWord(
        word: String,
        fromNative: Boolean
    ): Either<Failure, TranslatedYandexText>
}