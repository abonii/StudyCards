package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Card
import abm.co.domain.model.CardItem
import abm.co.domain.model.Category
import abm.co.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ServerRepository {

    val getUser: Flow<Either<Failure, User?>>
    val getCategories: Flow<Either<Failure, List<Category>>>

    suspend fun getCategory(id: String): Flow<Either<Failure, CardItem>>
    suspend fun createCategory(category: Category): Either<Failure, Unit>
    suspend fun updateCategory(category: Category): Either<Failure, Unit>

    suspend fun getCard(id: String): Flow<Either<Failure, Card>>
    suspend fun createCard(card: Card): Either<Failure, Unit>
    suspend fun updateCard(card: Card): Either<Failure, Unit>
}
