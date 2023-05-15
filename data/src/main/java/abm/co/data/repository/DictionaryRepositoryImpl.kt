package abm.co.data.repository

import abm.co.data.model.oxford.toDomain
import abm.co.data.model.qualifier.OxfordNetwork
import abm.co.data.model.qualifier.TypeEnum
import abm.co.data.model.qualifier.YandexNetwork
import abm.co.data.remote.OxfordApiService
import abm.co.data.remote.YandexApiService
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.safeCall
import abm.co.domain.model.oxford.OxfordTranslationResponse
import abm.co.domain.repository.ConfigRepository
import abm.co.domain.repository.DictionaryRepository
import abm.co.domain.repository.LanguagesRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@ActivityRetainedScoped
class DictionaryRepositoryImpl @Inject constructor(
    @OxfordNetwork(TypeEnum.APISERVICE) private val oxfordApiService: OxfordApiService,
    @YandexNetwork(TypeEnum.APISERVICE) private val yandexApiService: YandexApiService,
    private val languagesRepository: LanguagesRepository,
    private val configRepository: ConfigRepository
) : DictionaryRepository {

    override suspend fun getOxfordWord(
        word: String, fromNative: Boolean
    ): Either<Failure, OxfordTranslationResponse> =
        safeCall {
            val config = configRepository.getConfig().firstOrNull()?.asRight?.b
                ?: throw Throwable("Couldn't find your config, please contact support")
            val myNativeLang = languagesRepository.getNativeLanguage().firstOrNull()
                ?: throw Throwable("Couldn't find your native language")
            val myLearningLang = languagesRepository.getLearningLanguage().firstOrNull()
                ?: throw Throwable("Couldn't find your learning language")
            val firstLang = if (fromNative) myNativeLang else myLearningLang
            val secondLang = if (fromNative) myLearningLang else myNativeLang
            oxfordApiService.getWordTranslations(
                sourceLang = firstLang.code,
                targetLang = secondLang.code,
                wordId = word,
                api_id = config.oxfordId,
                api_key = config.oxfordKey
            ).toDomain()
        }

    override suspend fun getYandexWord(
        word: String, fromNative: Boolean
    ): Either<Failure, String> = safeCall {
        val config = configRepository.getConfig().firstOrNull()?.asRight?.b
            ?: throw Throwable("Couldn't find your config, please contact support")
        val myNativeLang = languagesRepository.getNativeLanguage().firstOrNull()
            ?: throw Throwable("Couldn't find your native language")
        val myLearningLang = languagesRepository.getLearningLanguage().firstOrNull()
            ?: throw Throwable("Couldn't find your learning language")
        val firstLang = if (fromNative) myNativeLang else myLearningLang
        val secondLang = if (fromNative) myLearningLang else myNativeLang
        yandexApiService.getWordTranslations(
            APIKey = config.yandexKey,
            textToTranslate = word,
            lang = "${firstLang.code}-${secondLang.code}"
        )
    }
}