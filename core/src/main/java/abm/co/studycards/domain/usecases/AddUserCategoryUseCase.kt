package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AddUserCategoryUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    suspend operator fun invoke(category: Category) {
        serverCloudRepository.addCategory(category)
    }
}