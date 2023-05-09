package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Card
import abm.co.domain.model.Category
import abm.co.domain.model.User
import abm.co.domain.model.config.Config
import kotlinx.coroutines.flow.Flow

interface ServerRepository {

    val getUser: Flow<Either<Failure, User?>>
    val getUserCategories: Flow<Either<Failure, List<Category>>>

    suspend fun createUserCategory(category: Category): Either<Failure, Category>
    suspend fun updateUserCategory(category: Category): Either<Failure, Unit>

    suspend fun createUserCard(card: Card): Either<Failure, Unit>
    suspend fun updateUserCard(card: Card): Either<Failure, Unit>

    suspend fun getUserCards(categoryID: String): Flow<Either<Failure, List<Card>>>

    suspend fun copyExploreCategoryToUserCollection(
        categoryID: String
    ): Either<Failure, Unit>

    val getCategories: Flow<Either<Failure, List<Category>>>
    suspend fun copyUserCategoryToExploreCollection(
        categoryID: String
    ): Either<Failure, Unit>

    suspend fun removeCategory(categoryID: String): Either<Failure, Unit>

    suspend fun removeUserDatabase()
    fun getConfig(): Flow<Either<Failure, Config>>
}
