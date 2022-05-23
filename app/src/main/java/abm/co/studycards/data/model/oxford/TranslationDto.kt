package abm.co.studycards.data.model.oxford

import abm.co.studycards.util.stripAccents

data class TranslationDto(val language: String?, val text: String?) {
    fun getNormalTranslation() = text?.stripAccents()
}
