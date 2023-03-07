package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.repository.DictionaryRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetYandexTranslatedWordUseCase @Inject constructor(
    private val serverCloudRepository: DictionaryRepository
) {
    suspend operator fun invoke(
        word: String,
        sourceLang: String,
        targetLang: String,
        yandexApiKey: String
    ) = serverCloudRepository.getYandexWord(word, sourceLang, targetLang, yandexApiKey)
}