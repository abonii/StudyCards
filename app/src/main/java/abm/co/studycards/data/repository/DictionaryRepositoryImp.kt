package abm.co.studycards.data.repository

import abm.co.studycards.data.network.oxford.OxfordApiService
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.network.yandex.YandexApiService
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import abm.co.studycards.di.qualifier.YandexNetwork
import abm.co.studycards.util.Constants.YANDEX_API_KEY
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


class DictionaryRepositoryImp @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE)
    private val oxfordApiService: OxfordApiService,
    @YandexNetwork(TypeEnum.APISERVICE)
    private val yandexApiService: YandexApiService,
) : DictionaryRepository {
    override suspend fun getOxfordWord(word: String, sl: String, tl: String) =
        safeApiCall(Dispatchers.IO) {
            oxfordApiService.getWordTranslations(
                sourceLang = sl,
                targetLang = tl,
                wordId = word
            )
        }

    override suspend fun getYandexWord(word: String, sl: String, tl: String) =
        safeApiCall(Dispatchers.IO) {
            yandexApiService.getWordTranslations(
                APIKey = YANDEX_API_KEY,
                textToTranslate = word,
                lang = "$sl-$tl"
            )
        }
}