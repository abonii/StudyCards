package abm.co.studycards.util

object Constants {
    const val CAN_TRANSLATE_EVERY_DAY_ANONYMOUS = 4
    const val CAN_TRANSLATE_EVERY_DAY = 12
    const val CATEGORIES_REF = "categories"
    const val WORDS_REF = "words"
    const val USER_ID = "user_id"
    const val USERS_REF = "users"
    const val DEBUG: Boolean = true
    const val ONE_TIME_CYCLE_GAME = 5
    const val BASE_URL_OXFORD = "https://od-api.oxforddictionaries.com:443/api/v2/"
    const val OXFORD_APP_ID = "36a89c65"
    const val OXFORD_API_KEY = "e9f074daee3a6d61e36becf42160e209"
    const val BASE_URL_YANDEX = "https://translate.yandex.net/api/v1.5/tr.json/"
    const val YANDEX_API_KEY = "trnsl.1.1.20210907T125259Z.71d59d2ddc18ec0c.3506adb6604f097ef464b23ac576f5e0a618893c"
    const val SHOULD_I_OPEN_PROFILE_FRAGMENT = "OPEN_PROFILE_FRAGMENT"
    const val VOCABULARY_NUM_TABS = 3
    const val VOCABULARY_TAB_POSITION = "VOCABULARY_TAB_POSITION"
    const val EXAMPLES_SEPARATOR = "\n"
    const val TRANSLATIONS_SEPARATOR = ", "
    const val REQUEST_IMAGE_KEY = "REQUEST_IMAGE_KEY"
    const val REQUEST_DICTIONARY_KEY = "REQUEST_DICTIONARY_KEY"
    const val REQUEST_CATEGORY_KEY = "REQUEST_CATEGORY_KEY"
    val OXFORD_CAN_TRANSLATE_MAP = mapOf(
        "en" to listOf("ar", "zh", "de", "it", "ru"),
        "ar" to listOf("en"),
        "zh" to listOf("en"),
        "de" to listOf("en"),
        "it" to listOf("en"),
        "ru" to listOf("en")
    )
}