package abm.co.data.utils

import com.android.billingclient.api.BillingClient.SkuType.INAPP

object BaseURLs {
    const val BASE_URL_OXFORD = "https://od-api.oxforddictionaries.com:443/api/v2/"
    const val BASE_URL_YANDEX = "https://translate.yandex.net/api/v1.5/tr.json/"
    const val DEV_ACCOUNT_LINK = "https://t.me/studycardsdev"

    val AN_APP_SKUS =
        listOf("translation_1000_times", "translation_500_times", "translation_250_times")
    const val PRODUCT_TYPE = INAPP
}