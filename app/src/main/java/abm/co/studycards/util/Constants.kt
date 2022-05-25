package abm.co.studycards.util

import com.android.billingclient.api.BillingClient.SkuType.INAPP

object Constants {
    const val TAG_ERROR = "IM_CHECKING_ERROR"
    const val TAG = "IM_CHECKING"
    const val APP_NAME = "StudyCards: Vocabulary Builder"
    const val ADJUST_DAY_BOUGHT_USER = 5
    const val ONE_TIME_CYCLE_GAME = 5
    const val VOCABULARY_NUM_TABS = 3

    const val BASE_URL_OXFORD = "https://od-api.oxforddictionaries.com:443/api/v2/"
    const val BASE_URL_YANDEX = "https://translate.yandex.net/api/v1.5/tr.json/"
    const val DEV_ACCOUNT_LINK = "https://t.me/studycardsdev"
    const val DEBUG = false

    const val EXAMPLES_SEPARATOR = "\n"
    const val TRANSLATIONS_SEPARATOR = ", "
    const val PRODUCT_TYPE = INAPP
    val AN_APP_SKUS =
        listOf("translation_1000_times", "translation_500_times", "translation_250_times")

    const val CATEGORIES_REF = "categories"
    const val WORDS_REF = "words"
    const val CONFIG_REF = "config"
    const val USER_ID = "user_id"
    const val USER_REF = "users"
    const val EXPLORE_REF = "explore"
    const val SETS_REF = "sets"
    const val NAME_REF = "name"

    const val VOCABULARY_TAB_POSITION = "VOCABULARY_TAB_POSITION"
    const val REQUEST_SYSTEM_LANGUAGE_KEY = "REQUEST_SYSTEM_LANGUAGE_KEY"
    const val REQUEST_DICTIONARY_KEY = "REQUEST_DICTIONARY_KEY"
    const val REQUEST_CATEGORY_KEY = "REQUEST_CATEGORY_KEY"
    const val VERIFY_PRODUCT_FUN = "verifyProduct"

    val OXFORD_CAN_TRANSLATE_MAP = mapOf(
        "en" to listOf("ar", "zh", "de", "it", "ru"),
        "ar" to listOf("en"),
        "zh" to listOf("en"),
        "de" to listOf("en"),
        "it" to listOf("en"),
        "ru" to listOf("en")
    )
}