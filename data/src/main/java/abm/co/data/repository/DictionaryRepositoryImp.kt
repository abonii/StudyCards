package abm.co.data.repository

import abm.co.data.model.qualifier.OxfordNetwork
import abm.co.data.model.qualifier.TypeEnum
import abm.co.data.model.qualifier.YandexNetwork
import abm.co.data.remote.OxfordApiService
import abm.co.data.remote.YandexApiService
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.safeCall
import abm.co.domain.repository.DictionaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepositoryImp @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE) private val oxfordApiService: OxfordApiService,
    @YandexNetwork(TypeEnum.APISERVICE) private val yandexApiService: YandexApiService,
) : DictionaryRepository {
    override suspend fun getOxfordWord(
        word: String, sourceLang: String, targetLang: String,
        oxfordApiId: String, oxfordApiKey: String
    ): Either<Failure, Nothing> =
        safeCall {
            oxfordApiService.getWordTranslations(
                sourceLang = sourceLang,
                targetLang = targetLang,
                wordId = word,
                api_id = oxfordApiId,
                api_key = oxfordApiKey
            )
        }

    override suspend fun getYandexWord(
        word: String,
        sourceLang: String,
        targetLang: String,
        yandexApiKey: String
    ): Either<Failure, Nothing> = safeCall {
        yandexApiService.getWordTranslations(
            APIKey = yandexApiKey,
            textToTranslate = word,
            lang = "$sourceLang-$targetLang"
        )
    }
}