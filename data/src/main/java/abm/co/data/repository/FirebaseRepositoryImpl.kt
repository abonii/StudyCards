package abm.co.data.repository

import abm.co.data.datastore.LanguagesDataStore
import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseRef.CATEGORY_REF
import abm.co.data.model.DatabaseRef.CONFIG_REF
import abm.co.data.model.DatabaseRef.ROOT_REF
import abm.co.data.model.DatabaseRef.USER_ID
import abm.co.data.model.DatabaseRef.USER_PROPERTIES_REF
import abm.co.data.model.DatabaseRef.USER_REF
import abm.co.data.model.card.CategoryDTO
import abm.co.data.model.card.toDTO
import abm.co.data.model.card.toDomain
import abm.co.data.model.user.UserDTO
import abm.co.data.model.user.toDomain
import abm.co.data.utils.asFlow
import abm.co.data.utils.firebaseError
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.base.safeCall
import abm.co.domain.model.Card
import abm.co.domain.model.CardItem
import abm.co.domain.model.Category
import abm.co.domain.repository.ServerRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mocklets.pluto.PlutoLog
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull

@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    @Named(USER_PROPERTIES_REF) private val userPropertiesReference: DatabaseReference,
    @Named(CONFIG_REF) private var configReference: DatabaseReference,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @Named(ROOT_REF) private val root: DatabaseReference,
    @Named(USER_ID) private val userId: String,
    languagesDataStore: LanguagesDataStore,
    private val gson: Gson
) : ServerRepository {

    private val categoryWithLanguagesRef = combine(
        languagesDataStore.getNativeLanguage(),
        languagesDataStore.getLearningLanguage()
    ) { native, learning ->
        root.child(USER_REF)
            .child(userId)
            .child(CATEGORY_REF)
            .child(native?.code ?: "en")
            .child(learning?.code ?: "en")
            .apply {
                keepSynced(true)
            }
    }.distinctUntilChanged()

    override val getUser = userPropertiesReference.asFlow(
        scope = coroutineScope,
        converter = { snapshot ->
            val map = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any?>?>() {})
            val json = gson.toJson(map)
            val userDTO = gson.fromJson(json, UserDTO::class.java)
            userDTO?.toDomain()
        }
    )

    override val getCategories: Flow<Either<Failure, List<Category>>> = callbackFlow {
        var previousListener: Pair<DatabaseReference, ValueEventListener>? = null
        categoryWithLanguagesRef.collectLatest { reference ->
            val listener = reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val items = ArrayList<CategoryDTO>()
                        snapshot.children.map { category ->
                            try {
                                category.getValue(CategoryDTO::class.java)?.let { items.add(it) }
                            } catch (e: DatabaseException) {
                                PlutoLog.e("getCategories", e.message, e.cause)
                            }
                        }
                        trySend(
                            Either.Right(items.sortedBy { it.bookmarked }.map { it.toDomain() })
                        ).isSuccess
                    } catch (e: Exception) {
                        trySend(Either.Left(e.firebaseError().mapToFailure())).isSuccess
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Either.Left(error.toException().firebaseError().mapToFailure()))
                        .isSuccess
                }
            })
            previousListener?.let { it.first.removeEventListener(it.second) }
            previousListener = reference to listener
        }
    }

    override suspend fun getCategory(id: String): Flow<Either<Failure, CardItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCard(id: String): Flow<Either<Failure, Card>> {
        TODO("Not yet implemented")
    }

    override suspend fun createCategory(category: Category): Either<Failure, Unit> {
        return safeCall {
            val ref = categoryWithLanguagesRef.firstOrNull()?.push()
            ref?.setValue(
                category.copy(
                    id = ref.key ?: "category_id"
                ).toDTO()
            )
        }
    }

    override suspend fun updateCategory(category: Category): Either<Failure, Unit> {
        return safeCall {
            categoryWithLanguagesRef.firstOrNull()?.updateChildren(
                mapOf(
                    category.id to category.toDTO()
                )
            )
        }
    }

    override suspend fun createCard(card: Card): Either<Failure, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCard(card: Card): Either<Failure, Unit> {
        TODO("Not yet implemented")
    }
}
