package abm.co.data.remote

import abm.co.data.model.yandex.TranslatedYandexTextDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexApiService {
    @GET("translate")
    suspend fun getWordTranslations(
        @Query("key") APIKey: String,
        @Query("text") textToTranslate: String,
        @Query("lang") lang: String,
    ): TranslatedYandexTextDTO
}