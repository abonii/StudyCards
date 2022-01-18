package abm.co.studycards.data.network.yandex

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.yandex.TranslatedText
import abm.co.studycards.util.Constants

interface YandexApiServiceHelper {
    suspend fun getWordTranslations(
        textToTranslate: String,
    ): ResultWrapper<TranslatedText>
}