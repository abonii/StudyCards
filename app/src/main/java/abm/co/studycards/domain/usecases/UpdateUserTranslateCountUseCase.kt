package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UpdateUserTranslateCountUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    suspend operator fun invoke(count: Long) = serverCloudRepository.updateUserTranslateCount(count)
}