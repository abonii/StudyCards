package abm.co.studycards.data.repository

import abm.co.studycards.data.network.oxford.OxfordApiService
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.network.yandex.YandexApiService
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import abm.co.studycards.di.qualifier.YandexNetwork
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DictionaryRepositoryImp @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE)
    private val oxfordApiService: OxfordApiService,
    @YandexNetwork(TypeEnum.APISERVICE)
    private val yandexApiService: YandexApiService,
) : DictionaryRepository {
    override suspend fun getOxfordWord(
        word: String,
        sl: String,
        tl: String,
        oxfordApiId: String,
        oxfordApiKey: String
    ) =
        safeApiCall(Dispatchers.IO) {
            oxfordApiService.getWordTranslations(
                sourceLang = sl,
                targetLang = tl,
                wordId = word,
                oxfordApiId,
                oxfordApiKey
            )
        }

    override suspend fun getYandexWord(word: String, sl: String, tl: String, yandexApiKey: String) =
        safeApiCall(Dispatchers.IO) {
            yandexApiService.getWordTranslations(
                APIKey = yandexApiKey,
                textToTranslate = word,
                lang = "$sl-$tl"
            )
        }
}