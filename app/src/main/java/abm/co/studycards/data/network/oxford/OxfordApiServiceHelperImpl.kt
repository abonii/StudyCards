package abm.co.studycards.data.network.oxford

import abm.co.studycards.data.ResultWrapper
import abm.co.studycards.data.model.oxford.RetrieveEntry
import abm.co.studycards.data.network.safeApiCall
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OxfordApiServiceHelperImpl @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE) private val oxfordApiService: OxfordApiService,
    private val prefs: Prefs
) : OxfordApiServiceHelper {
    override suspend fun getWordTranslations(
        word: String
    ): ResultWrapper<RetrieveEntry> {
        return safeApiCall(Dispatchers.IO) {
            oxfordApiService.getWordTranslations(
                prefs.getSourceLanguage(),
                prefs.getTargetLanguage(),
                word
            )
        }
    }

}

