package abm.co.studycards.domain.usecases

import abm.co.studycards.R
import abm.co.studycards.domain.model.LearnOrKnown
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class GetUserWordsUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {

    operator fun invoke(type: LearnOrKnown): Flow<ResultWrapper<List<Word>>> {
        return serverCloudRepository.fetchUserWords().map { wrapper ->
            if (wrapper is ResultWrapper.Success) {
                val words =
                    wrapper.value
                        .filter { LearnOrKnown.getType(it.learnOrKnown) == type }
                if (words.isEmpty()) {
                    ResultWrapper.Error(res = R.string.empty_in_vocabulary)
                } else
                    ResultWrapper.Success(words)
            } else {
                wrapper
            }
        }
    }
}