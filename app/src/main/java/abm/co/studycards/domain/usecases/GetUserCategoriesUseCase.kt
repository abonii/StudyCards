package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetUserCategoriesUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    operator fun invoke(): Flow<ResultWrapper<List<Category>>> {
        return serverCloudRepository.fetchUserCategories()
    }
}