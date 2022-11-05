package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.repository.DictionaryRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetOxfordTranslatedResultUseCase @Inject constructor(
    private val serverCloudRepository: DictionaryRepository
) {

    suspend operator fun invoke(
        word: String, sourceLang: String, targetLang: String,
        oxfordApiId: String, oxfordApiKey: String
    ) = serverCloudRepository.getOxfordWord(word, sourceLang, targetLang, oxfordApiId, oxfordApiKey)
}