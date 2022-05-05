package abm.co.studycards.util

import com.android.billingclient.api.BillingClient

object Constants {
    const val TAG = "IM_CHECKING"
    const val APP_NAME = "Study Cards"
    const val CAN_TRANSLATE_EVERY_DAY_ANONYMOUS = 4
    const val CAN_TRANSLATE_EVERY_DAY = 12
    const val ONE_TIME_CYCLE_GAME = 5
    const val VOCABULARY_NUM_TABS = 3

    const val BASE_URL_OXFORD = "https://od-api.oxforddictionaries.com:443/api/v2/"
    const val BASE_URL_YANDEX = "https://translate.yandex.net/api/v1.5/tr.json/"
    const val DEV_ACCOUNT_LINK = "https://t.me/studycardsdev"
    const val YANDEX_KEY = "yandex_key"
    const val OXFORD_ID = "oxford_id"
    const val OXFORD_KEY= "oxford_key"
    const val DEBUG = false

    const val EXAMPLES_SEPARATOR = "\n"
    const val TRANSLATIONS_SEPARATOR = ", "
    const val PRODUCT_TYPE = BillingClient.SkuType.INAPP
    val AN_APP_SKUS = listOf("translation_1000_times","translation_500_times","translation_250_times")

    const val CATEGORIES_REF = "categories"
    const val WORDS_REF = "words"
    const val API_REF = "api"
    const val USER_ID = "user_id"
    const val USERS_REF = "users"
    const val USER_REF = "user"
    const val EXPLORE_REF = "explore"
    const val SETS_REF = "sets"

    const val VOCABULARY_TAB_POSITION = "VOCABULARY_TAB_POSITION"
    const val REQUEST_SYSTEM_LANGUAGE_KEY = "REQUEST_SYSTEM_LANGUAGE_KEY"
    const val REQUEST_DICTIONARY_KEY = "REQUEST_DICTIONARY_KEY"
    const val CAN_TRANSLATE_TIME_EVERY_DAY= "canTranslateTimeEveryDay"
    const val CAN_TRANSLATE_TIME_IN_MILLS= "canTranslateTimeInMills"
    const val SELECTED_LANGUAGES= "selected_languages"
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