package abm.co.studycards.data.model.yandex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TranslatedText {
    var code: Int? = null
    var lang: String? = null
    var text: List<String>? = null
}