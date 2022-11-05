package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetUserCategoryUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    operator fun invoke(categoryId: String) = serverCloudRepository.getTheCategory(categoryId)
}