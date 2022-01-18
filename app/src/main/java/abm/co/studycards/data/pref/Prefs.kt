package abm.co.studycards.data.pref

interface Prefs {

    companion object {
        const val ACCESS_TOKEN = "STUDY_CARD_ACCESS_TOKEN"
        const val SHARED_PREFERENCES = "STUDY_CARD_SHARED_PREF"
        const val USER_ID = "STUDY_CARD_USER_ID"
        const val SELECTED_APP_LANGUAGE = "STUDY_CARD_LOCALE_SELECTED_LANGUAGE"
        const val SOURCE_LANGUAGE: String = "ABO_FLASH_CARD_NATIVE_LANGUAGE"
        const val TARGET_LANGUAGE: String = "ABO_FLASH_CARD_TARGET_LANGUAGE"
    }

    fun getAccessToken(): String
    fun setAccessToken(value: String)
    fun removeAccessToken()

    fun getUserId(): Int
    fun setUserId(value: Int)
    fun removeUserId()

    fun getAppLanguage(): String
    fun setAppLanguage(value: String)


    fun getSourceLanguage(): String
    fun setSourceLanguage(value: String)

    fun getTargetLanguage(): String
    fun setTargetLanguage(value: String)


}