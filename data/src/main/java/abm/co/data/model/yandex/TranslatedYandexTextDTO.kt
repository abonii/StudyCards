package abm.co.data.model.yandex

import abm.co.domain.model.yandex.TranslatedYandexText
import androidx.annotation.Keep

@Keep
data class TranslatedYandexTextDTO (
    var code: Int?,
    var lang: String?,
    var text: List<String>?
) {
    fun toDomain() = TranslatedYandexText(
        code = code,
        lang = lang,
        text = text
    )
}