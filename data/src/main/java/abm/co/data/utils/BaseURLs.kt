package abm.co.data.utils

import com.android.billingclient.api.BillingClient.SkuType.INAPP

object BaseURLs {
    const val BASE_URL_OXFORD = "https://od-api.oxforddictionaries.com:443/api/v2/"
    const val BASE_URL_YANDEX = "https://translate.yandex.net/api/v2/translate/"
    /*lookup?key={APIkey}&lang={en-ru}&{text=time}*/
    const val BASE_URL_YANDEX_DICTIONARY = "https://dictionary.yandex.net/api/v1/dicservice.json/"
    const val DEV_ACCOUNT_LINK = "https://t.me/studycardsdev"

    val AN_APP_SKUS =
        listOf("translation_1000_times", "translation_500_times", "translation_250_times")
    const val PRODUCT_TYPE = INAPP
}