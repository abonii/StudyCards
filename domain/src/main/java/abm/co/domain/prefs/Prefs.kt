package abm.co.domain.prefs

import abm.co.domain.model.Language

interface Prefs {

    companion object {
        const val SHARED_PREFERENCES = "STUDY_CARD_SHARED_PREF"
        const val APP_LANGUAGE = "APP_LANGUAGE"
    }

    fun getAppLanguage(): Language?
    fun setAppLanguage(value: Language)
}
