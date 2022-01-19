package abm.co.studycards.data.network.yandex

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.yandex.TranslatedText

interface YandexApiServiceHelper {
    suspend fun getWordTranslations(
        textToTranslate: String,
        sl: String, tl: String
    ): ResultWrapper<TranslatedText>
}