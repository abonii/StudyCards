package abm.co.studycards.data.network.oxford

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.oxford.RetrieveEntry

interface OxfordApiServiceHelper {
    suspend fun getWordTranslations(
        word: String,
        sl: String, tl: String
    ): ResultWrapper<RetrieveEntry>
}