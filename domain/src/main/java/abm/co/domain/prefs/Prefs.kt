package abm.co.domain.prefs

import abm.co.domain.model.Language

interface Prefs {

    companion object {
        const val ACCESS_TOKEN = "STUDY_CARD_ACCESS_TOKEN"
        const val SHARED_PREFERENCES = "STUDY_CARD_SHARED_PREF"
        const val USER_ID = "STUDY_CARD_USER_ID"
        const val APP_LANGUAGE = "STUDY_CARD_LOCALE_APP_LANGUAGE"
        const val NATIVE_LANGUAGE: String = "ABO_FLASH_CARD_NATIVE_LANGUAGE"
        const val LEARNING_LANGUAGE: String = "ABO_FLASH_CARD_LEARNING_LANGUAGE"
        const val IS_PREMIUM: String = "ABO_FLASH_CARD_IS_PREMIUM"
        const val IS_FIRST_TIME: String = "ABO_FLASH_CARD_IS_FIRST_TIME"
    }

    fun getAccessToken(): String
    fun setAccessToken(value: String)
    fun removeAccessToken()

    fun getUserId(): Int
    fun setUserId(value: Int)
    fun removeUserId()

    fun getAppLanguage(): Language?
    fun setAppLanguage(value: Language)


    fun getNativeLanguage(): Language?
    fun setNativeLanguage(value: Language)

    fun getLearningLanguage(): Language?
    fun setLearningLanguage(value: Language)

    fun getIsPremium(): Boolean
    fun setIsPremium(value: Boolean)

    fun getIsFirstTime(): Boolean
    fun getIsFirstTime(value: Boolean)
}
