package abm.co.studycards.data.network.oxford

import abm.co.studycards.data.model.oxford.RetrieveEntry
import retrofit2.http.GET
import retrofit2.http.Path

interface OxfordApiService {

    @GET("translations/{source_lang}/{target_lang}/{word_id}?strictMatch=false")
    suspend fun getWordTranslations(
        @Path("source_lang") sourceLang: String,
        @Path("target_lang") targetLang: String,
        @Path("word_id") wordId: String
    ): RetrieveEntry

}