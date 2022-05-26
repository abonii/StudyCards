package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.repository.ServerCloudRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@ViewModelScoped
class GetExploreCategoryUseCase @Inject constructor(
    private val serverCloudRepository: ServerCloudRepository
) {
    operator fun invoke(
        setId: String,
        categoryId: String
    ): Triple<SharedFlow<ResultWrapper<Category>>, DatabaseReference, ValueEventListener> {
        return serverCloudRepository.getExploreCategory(setId, categoryId)
    }
}