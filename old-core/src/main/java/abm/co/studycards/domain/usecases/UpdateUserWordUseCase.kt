package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateUserWordUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository,
    private val deleteUserWordUseCase: DeleteUserWordUseCase,
    private val addUserWordUseCase: AddUserWordUseCase
) {
    suspend operator fun invoke(oldWord: Word?, newWord: Word) {
        if (oldWord != null && oldWord.categoryID != newWord.categoryID) {
            deleteUserWordUseCase(oldWord)
            addUserWordUseCase(newWord)
        } else {
            serverCloudRepository.updateWord(newWord)
        }
    }

    suspend fun repeatType(word: Word) {
        serverCloudRepository.updateWordRepeatType(word)
    }

    suspend fun learnType(word: Word) {
        serverCloudRepository.updateWordLearnType(word)
    }
}