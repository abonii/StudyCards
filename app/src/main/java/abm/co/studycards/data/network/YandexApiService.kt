package abm.co.studycards.data.network

import abm.co.studycards.data.model.yandex.TranslatedYandexTextDto
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexApiService {
    @GET("translate")
    suspend fun getWordTranslations(
        @Query("key") APIKey: String,
        @Query("text") textToTranslate: String,
        @Query("lang") lang: String,
    ): TranslatedYandexTextDto
}