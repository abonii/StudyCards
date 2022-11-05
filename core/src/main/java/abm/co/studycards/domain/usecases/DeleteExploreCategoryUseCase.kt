package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteExploreCategoryUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    suspend operator fun invoke(setId: String, category: Category) =
        serverCloudRepository.deleteExploreCategory(setId, category)

}