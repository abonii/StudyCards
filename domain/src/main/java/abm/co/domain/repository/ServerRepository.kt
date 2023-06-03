package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Card
import abm.co.domain.model.CardKind
import abm.co.domain.model.Category
import abm.co.domain.model.User
import abm.co.domain.model.config.Config
import abm.co.domain.model.library.Book
import kotlinx.coroutines.flow.Flow

interface ServerRepository {

    val getUser: Flow<Either<Failure, User?>>

    val getUserCategories: Flow<Either<Failure, List<Category>>>
    suspend fun createUserCategory(category: Category): Either<Failure, Category>
    suspend fun createUserCard(card: Card): Either<Failure, Unit>
    suspend fun updateUserCard(card: Card): Either<Failure, Unit>
    suspend fun getUserCards(categoryID: String): Flow<Either<Failure, List<Card>>>
    suspend fun removeUserCategory(categoryID: String): Either<Failure, Unit>
    suspend fun removeUserCard(categoryID: String, cardID: String): Either<Failure, Unit>
    suspend fun updateUserCategory(
        id: String,
        bookmarked: Boolean? = null,
        name: String? = null
    ): Either<Failure, Unit>
    suspend fun updateUserCardKind(
        categoryID: String,
        cardID: String,
        kind: CardKind
    ): Either<Failure, Unit>
    suspend fun removeUserDatabase()

    val getCategories: Flow<Either<Failure, List<Category>>>
    suspend fun removeCategory(categoryID: String): Either<Failure, Unit>

    suspend fun copyExploreCategoryToUserCollection(categoryID: String): Either<Failure, Unit>
    suspend fun copyUserCategoryToExploreCollection(categoryID: String): Either<Failure, Unit>

    suspend fun addBook(book: Book): Either<Failure, Unit>
    val getLibrary: Flow<Either<Failure, List<Book>>>
}
