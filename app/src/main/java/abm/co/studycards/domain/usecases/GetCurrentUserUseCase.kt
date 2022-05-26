package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetCurrentUserUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    operator fun invoke() = serverCloudRepository.getCurrentUser()

}