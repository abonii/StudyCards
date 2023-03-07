package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.ParentSet
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ViewModelScoped
class GetExploreSetsUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    operator fun invoke(): StateFlow<ResultWrapper<List<ParentSet>>> {
        return serverCloudRepository.fetchExploreSets()
    }
}