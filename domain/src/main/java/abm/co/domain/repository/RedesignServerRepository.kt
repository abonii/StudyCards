package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Category
import abm.co.domain.model.explore.ExploreCategoryContext

interface RedesignServerRepository {
    suspend fun getExploreCategories(): Either<Failure, List<Category>>
    suspend fun getExploreCategory(id: String): Either<Failure, ExploreCategoryContext>
    suspend fun addExploreCategoryToUserCategory(request: ExploreCategoryContext): Either<Failure, Unit>
}
