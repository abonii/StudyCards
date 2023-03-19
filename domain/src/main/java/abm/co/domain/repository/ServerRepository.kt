package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Card
import abm.co.domain.model.CardItem
import abm.co.domain.model.Category
import abm.co.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    suspend fun getUser(): Flow<Either<Failure, User?>>
    suspend fun getCategories(): Flow<Either<Failure, List<Category>>>
    suspend fun getCategory(id: String): Flow<Either<Failure, CardItem>>
    suspend fun getCard(id: String): Flow<Either<Failure, Card>>
}
