package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure

interface DictionaryRepository {
    suspend fun getOxfordWord(
        word: String,
        sourceLang: String,
        targetLang: String,
        oxfordApiId: String,
        oxfordApiKey: String
    ): Either<Failure, Nothing>

    suspend fun getYandexWord(
        word: String,
        sourceLang: String,
        targetLang: String,
        yandexApiKey: String
    ): Either<Failure, Nothing>
}