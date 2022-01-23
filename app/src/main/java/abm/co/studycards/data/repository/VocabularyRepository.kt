package abm.co.studycards.data.repository

import abm.co.studycards.data.network.oxford.OxfordApiServiceHelper
import abm.co.studycards.data.network.yandex.YandexApiServiceHelper
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import abm.co.studycards.di.qualifier.YandexNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepository @Inject constructor(
    @OxfordNetwork(TypeEnum.APIHELPER)
    private val oxfordApiHelper: OxfordApiServiceHelper,
    @YandexNetwork(TypeEnum.APIHELPER)
    private val yandexApiServiceHelper: YandexApiServiceHelper,
) {
    suspend fun getOxfordWord(word: String, sl: String, tl: String) =
        oxfordApiHelper.getWordTranslations(word, sl, tl)

    suspend fun getYandexWord(word: String, sl: String, tl: String) =
        yandexApiServiceHelper.getWordTranslations(word, sl, tl)
}