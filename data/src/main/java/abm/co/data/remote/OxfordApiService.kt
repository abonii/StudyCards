package abm.co.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface OxfordApiService {

    @GET("translations/{source_lang}/{target_lang}/{word_id}?strictMatch=false")
    suspend fun getWordTranslations(
        @Path("source_lang") sourceLang: String,
        @Path("target_lang") targetLang: String,
        @Path("word_id") wordId: String,
        @Header("app_id") api_id:String,
        @Header("app_key") api_key:String,
    ): Nothing

}