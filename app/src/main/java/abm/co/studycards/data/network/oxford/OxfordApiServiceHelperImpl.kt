package abm.co.studycards.data.network.oxford

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.oxford.RetrieveEntry
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import android.util.Log
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OxfordApiServiceHelperImpl @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE) private val oxfordApiService: OxfordApiService,
) : OxfordApiServiceHelper {
    override suspend fun getWordTranslations(
        word: String,
        sl: String, tl: String
    ): ResultWrapper<RetrieveEntry> {
        return safeApiCall(Dispatchers.IO) {
            oxfordApiService.getWordTranslations(
                sl, tl, word
            )
        }
    }

}

