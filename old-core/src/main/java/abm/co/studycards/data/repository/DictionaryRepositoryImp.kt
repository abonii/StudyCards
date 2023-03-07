package abm.co.studycards.data.repository

import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.data.model.StudyCardsMapper
import abm.co.studycards.data.network.OxfordApiService
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.network.YandexApiService
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import abm.co.studycards.di.qualifier.YandexNetwork
import abm.co.studycards.domain.model.OxfordResult
import abm.co.studycards.domain.repository.DictionaryRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepositoryImp @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE) private val oxfordApiService: OxfordApiService,
    @YandexNetwork(TypeEnum.APISERVICE) private val yandexApiService: YandexApiService,
    private val mapper: StudyCardsMapper
) : DictionaryRepository {
    override suspend fun getOxfordWord(
        word: String, sourceLang: String, targetLang: String,
        oxfordApiId: String, oxfordApiKey: String
    ): ResultWrapper<OxfordResult> =
        safeApiCall(Dispatchers.IO) {
            mapper.mapOxfordDtoToModel(
                oxfordApiService.getWordTranslations(
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                    wordId = word,
                    api_id = oxfordApiId,
                    api_key = oxfordApiKey
                )
            )
        }

    override suspend fun getYandexWord(
        word: String,
        sourceLang: String,
        targetLang: String,
        yandexApiKey: String
    ): ResultWrapper<String> =
        safeApiCall(Dispatchers.IO) {
            yandexApiService.getWordTranslations(
                APIKey = yandexApiKey,
                textToTranslate = word,
                lang = "$sourceLang-$targetLang"
            ).text?.joinToString(", ") ?: ""
        }
}