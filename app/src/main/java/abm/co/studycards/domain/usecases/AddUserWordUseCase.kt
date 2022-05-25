package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AddUserWordUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    suspend operator fun invoke(word: Word) {
        serverCloudRepository.addWord(word)
    }
}