package abm.co.domain.usecase

import abm.co.domain.base.Either
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onLeft
import abm.co.domain.base.onRight
import abm.co.domain.base.onSuccess
import abm.co.domain.functional.safeLet
import abm.co.domain.model.oxford.OxfordTranslationResponse
import abm.co.domain.model.yandex.TranslatedYandexText
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.DictionaryRepository
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetWordInfoUseCase(
    private val languagesRepository: LanguagesRepository,
    private val serverRepository: ServerRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val dictionaryRepository: DictionaryRepository
) {

    private val translateCountFlow = serverRepository.getUser.map {
        it.asRight?.b?.translateCounts
    }

    suspend operator fun invoke(
        word: String,
        fromNative: Boolean
    ): Either<Failure, Either<OxfordTranslationResponse, TranslatedYandexText>> {
        if (checkIfOxfordSupport(fromNative) && isItOneThanOneWord(word)) {
            fetchOxfordWord(word, fromNative)
                .onSuccess { either ->
                    either.onLeft {
                        return Either.Right(Either.Left(it))
                    }.onRight {
                        return Either.Right(Either.Right(it))
                    }
                }.onFailure {
                    return Either.Left(it)
                }
        } else {
            fetchYandexWord(word, fromNative)
                .onSuccess {
                    val either: Either<OxfordTranslationResponse, TranslatedYandexText> =
                        Either.Right(it)
                    return Either.Right(either)
                }.onFailure {
                    return Either.Left(it)
                }
        }
        return Either.Left(
            Failure.DefaultAlert(
                expectedMessage = "Something went wrong"
            )
        )
    }


    private suspend fun fetchOxfordWord(
        word: String,
        fromNative: Boolean
    ): Either<Failure, Either<OxfordTranslationResponse, TranslatedYandexText>> {
        val translateCount = translateCountFlow.firstOrNull() ?: 0
        if (translateCount <= 0) {
            Either.Left(
                Failure.FailureSnackbar(
                    expectedMessage = ExpectedMessage.String(
                        value = "You don't have any translation attempts, you can buy it"
                    )
                )
            )
        } else {
            dictionaryRepository.getOxfordWord(
                word = word,
                fromNative = fromNative
            ).onFailure {
                fetchYandexWord(word, fromNative)
                    .onFailure {
                        return Either.Left(it)
                    }
                    .onSuccess {
                        return Either.Right(Either.Right(it))
                    }
            }.onSuccess { response ->
                authorizationRepository.updateUserTranslationCount(translateCount - 1)
                return Either.Right(Either.Left(response))
            }
        }
        return Either.Empty
    }

    private suspend fun fetchYandexWord(
        word: String,
        fromNative: Boolean
    ): Either<Failure, TranslatedYandexText> {
        val translateCount = translateCountFlow.firstOrNull() ?: 0
        if (translateCount <= 0) {
            return Either.Left(
                Failure.FailureSnackbar(
                    expectedMessage = ExpectedMessage.String(
                        value = "You don't have any translation attempts, you can get it for free in store"
                    )
                )
            )
        } else {
            dictionaryRepository.getYandexWord(
                word = word,
                fromNative = fromNative
            ).onFailure {
                return Either.Left(it)
            }.onSuccess { response ->
                authorizationRepository.updateUserTranslationCount(translateCount - 1)
                return Either.Right(response)
            }
        }
        return Either.Empty
    }

    private suspend fun checkIfOxfordSupport(
        fromNative: Boolean
    ): Boolean {
        val myNativeLang = languagesRepository.getNativeLanguage().firstOrNull()
        val myLearningLang = languagesRepository.getLearningLanguage().firstOrNull()
        safeLet(myNativeLang, myLearningLang) { native, learning ->
            return if (fromNative) {
                OXFORD_CAN_TRANSLATE_MAP[native.code]?.contains(learning.code) ?: false
            } else {
                OXFORD_CAN_TRANSLATE_MAP[learning.code]?.contains(native.code) ?: false
            }
        }
        return false
    }

    private fun isItOneThanOneWord(word: String): Boolean {
        return word.trim().split(" ", ",", ".", ";").size == 1
    }

    companion object {
        private val OXFORD_CAN_TRANSLATE_MAP = mapOf(
            "en" to listOf("ar", "zh", "de", "it", "ru"),
            "ar" to listOf("en"),
            "zh" to listOf("en"),
            "de" to listOf("en"),
            "it" to listOf("en"),
            "ru" to listOf("en")
        )

        fun build(
            languagesRepository: LanguagesRepository,
            serverRepository: ServerRepository,
            authorizationRepository: AuthorizationRepository,
            dictionaryRepository: DictionaryRepository
        ) = GetWordInfoUseCase(
            languagesRepository = languagesRepository,
            serverRepository = serverRepository,
            authorizationRepository = authorizationRepository,
            dictionaryRepository = dictionaryRepository
        )
    }
}
