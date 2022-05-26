package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.UserInfo
import abm.co.studycards.domain.repository.ServerCloudRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ViewModelScoped
class GetUserInfoUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    operator fun invoke(): StateFlow<UserInfo> = serverCloudRepository.fetchUserInfo()
}