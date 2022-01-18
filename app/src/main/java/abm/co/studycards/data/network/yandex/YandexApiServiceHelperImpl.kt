package abm.co.studycards.data.network.yandex

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.yandex.TranslatedText
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.di.qualifier.TypeEnum
import abm.co.studycards.di.qualifier.YandexNetwork
import abm.co.studycards.util.Constants
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexApiServiceHelperImpl @Inject constructor(
    @YandexNetwork(TypeEnum.APISERVICE)
    private val yandexApiService: YandexApiService,
    private val prefs: Prefs
) : YandexApiServiceHelper {

    override suspend fun getWordTranslations(
        textToTranslate: String,
    ): ResultWrapper<TranslatedText> {
        return safeApiCall(Dispatchers.IO) {
            yandexApiService.getWordTranslations(
                APIKey = Constants.yandex_api_key,
                textToTranslate = textToTranslate,
                lang = prefs.getSourceLanguage() + "-" + prefs.getTargetLanguage()
            )
        }
    }
}