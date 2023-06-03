package abm.co.data.repository

import abm.co.data.datastore.LanguagesDataStore
import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseRef.CARD_REF
import abm.co.data.model.DatabaseRef.CATEGORY_REF
import abm.co.data.model.DatabaseRef.EXPLORE_REF
import abm.co.data.model.DatabaseRef.LIBRARY_REF
import abm.co.data.model.DatabaseRef.ROOT_REF
import abm.co.data.model.DatabaseRef.USER_PROPERTIES_REF
import abm.co.data.model.DatabaseRef.USER_REF
import abm.co.data.model.card.CardDTO
import abm.co.data.model.card.CategoryDTO
import abm.co.data.model.card.toDTO
import abm.co.data.model.card.toDomain
import abm.co.data.model.library.BookDTO
import abm.co.data.model.library.toDTO
import abm.co.data.model.user.UserDTO
import abm.co.data.model.user.toDomain
import abm.co.data.utils.asFlow
import abm.co.data.utils.firebaseError
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.base.safeCall
import abm.co.domain.model.Card
import abm.co.domain.model.CardKind
import abm.co.domain.model.Category
import abm.co.domain.model.library.Book
import abm.co.domain.repository.ServerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mocklets.pluto.PlutoLog
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Named

@ActivityRetainedScoped
class FirebaseRepositoryImpl @Inject constructor(
    @Named(USER_PROPERTIES_REF) private val userPropertiesReference: DatabaseReference,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @Named(ROOT_REF) private val root: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
    private val languagesDataStore: LanguagesDataStore,
    private val gson: Gson
) : ServerRepository {

    private val userCategoryWithLanguagesRef = combine(
        languagesDataStore.getNativeLanguage(),
        languagesDataStore.getLearningLanguage()
    ) { native, learning ->
        root.child(USER_REF)
            .child(firebaseAuth.currentUser?.uid ?: "no-user-id")
            .child(CATEGORY_REF)
            .child(native?.code ?: "en")
            .child(learning?.code ?: "en")
            .apply {
                keepSynced(true)
            }
    }.distinctUntilChanged()

    private val categoryWithLanguagesRef = combine(
        languagesDataStore.getNativeLanguage(),
        languagesDataStore.getLearningLanguage()
    ) { native, learning ->
        root.child(EXPLORE_REF)
            .child(native?.code ?: "en")
            .child(learning?.code ?: "en")
            .apply {
                keepSynced(true)
            }
    }.distinctUntilChanged()

    private val libraryWithLanguagesRef = languagesDataStore.getLearningLanguage().map { learning ->
        root.child(LIBRARY_REF)
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

    override val getUserCategories: Flow<Either<Failure, List<Category>>> = callbackFlow {
        var previousListener: Pair<DatabaseReference, ValueEventListener>? = null
        userCategoryWithLanguagesRef.collectLatest { reference ->
            val listener = reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val items = ArrayList<Category>()
                        snapshot.children.map { category ->
                            try {
                                category.getValue(CategoryDTO::class.java)
                                    ?.let {
                                        val cardsCount = category
                                            .child(CARD_REF)
                                            .childrenCount
                                            .toInt()
                                        if (it.enabled == true) {
                                            items.add(it.toDomain(cardsCount))
                                        }
                                    }
                            } catch (e: DatabaseException) {
                                PlutoLog.e("getCategories", e.message, e.cause)
                            }
                        }
                        trySend(
                            Either.Right(
                                items.sortedByDescending { it.bookmarked }
                            )
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

    override val getCategories: Flow<Either<Failure, List<Category>>> = callbackFlow {
        var previousListener: Pair<DatabaseReference, ValueEventListener>? = null
        categoryWithLanguagesRef.collectLatest { reference ->
            val listener = reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val items = ArrayList<Category>()
                        snapshot.children.map { category ->
                            try {
                                category.getValue(CategoryDTO::class.java)
                                    ?.let {
                                        val cardsCount = category
                                            .child(CARD_REF)
                                            .childrenCount
                                            .toInt()
                                        if (it.enabled == true) {
                                            items.add(it.toDomain(cardsCount))
                                        }
                                    }
                            } catch (e: DatabaseException) {
                                PlutoLog.e("getCategories", e.message, e.cause)
                            }
                        }
                        trySend(
                            Either.Right(
                                items.sortedByDescending { it.bookmarked }
                            )
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

    override val getLibrary: Flow<Either<Failure, List<Book>>> = callbackFlow {
        var previousListener: Pair<DatabaseReference, ValueEventListener>? = null
        libraryWithLanguagesRef.collectLatest { reference ->
            val listener = reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val items = ArrayList<BookDTO>()
                        snapshot.children.map { item ->
                            try {
                                item.getValue(BookDTO::class.java)
                                    ?.let { items.add(it) }
                            } catch (e: DatabaseException) {
                                PlutoLog.e("getLibrary", e.message, e.cause)
                            }
                        }
                        trySend(
                            Either.Right(items.map { it.toDomain() })
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

    override suspend fun createUserCategory(category: Category): Either<Failure, Category> {
        return safeCall {
            val ref = userCategoryWithLanguagesRef.firstOrNull()?.push()
            val updatedCategory = category.copy(
                id = ref?.key ?: "category_id",
                creatorID = firebaseAuth.currentUser?.uid,
                creatorName = getUser.firstOrNull()?.asRight?.b?.name
                    ?: firebaseAuth.currentUser?.displayName
            )
            ref?.setValue(updatedCategory.toDTO())
            updatedCategory
        }
    }

    override suspend fun updateUserCategory(
        id: String,
        bookmarked: Boolean?,
        name: String?
    ): Either<Failure, Unit> {
        return safeCall {
            val categoryRef = userCategoryWithLanguagesRef.firstOrNull()
                ?.child(id) ?: return@safeCall
            name?.let { categoryRef.child(Category.title).setValue(it) }
            bookmarked?.let { categoryRef.child(Category.bookmarked).setValue(it) }
        }
    }

    override suspend fun createUserCard(card: Card): Either<Failure, Unit> {
        return safeCall {
            val cardDTO = card.toDTO()
            val ref = userCategoryWithLanguagesRef.firstOrNull()
                ?.child(cardDTO.categoryID)
                ?.child(CARD_REF)
                ?.child(cardDTO.id)?.push()
            ref?.setValue(
                cardDTO.copy(
                    id = ref.key ?: "card_id"
                )
            )
        }
    }

    override suspend fun updateUserCard(card: Card): Either<Failure, Unit> {
        return safeCall {
            val cardDTO = card.toDTO()
            userCategoryWithLanguagesRef.firstOrNull()
                ?.child(cardDTO.categoryID)
                ?.child(CARD_REF)
                ?.updateChildren(
                    mapOf(cardDTO.id to cardDTO)
                )
        }
    }

    override suspend fun updateUserCardKind(
        categoryID: String,
        cardID: String,
        kind: CardKind
    ): Either<Failure, Unit> {
        return safeCall {
            userCategoryWithLanguagesRef.firstOrNull()
                ?.child(categoryID)
                ?.child(CARD_REF)
                ?.child(cardID)
                ?.updateChildren(
                    mapOf(Card.kind to kind.toDTO())
                )
        }
    }

    override suspend fun getUserCards(categoryID: String): Flow<Either<Failure, List<Card>>> =
        callbackFlow {
            val categoryReference =
                userCategoryWithLanguagesRef.firstOrNull()?.child(categoryID)?.child(CARD_REF)
            val listener = categoryReference?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val items = ArrayList<CardDTO>()
                        snapshot.children.map { category ->
                            try {
                                category.getValue(CardDTO::class.java)
                                    ?.let { items.add(it) }
                            } catch (e: DatabaseException) {
                                PlutoLog.e("getCardItems", e.message, e.cause)
                            }
                        }
                        trySend(
                            Either.Right(items.map { it.toDomain() })
                        ).isSuccess
                    } catch (e: Exception) {
                        trySend(Either.Left(e.firebaseError().mapToFailure())).isSuccess
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    PlutoLog.e(
                        "DatabaseReference.asFlow",
                        "Error reading data: ${error.message}"
                    )
                    trySend(
                        Either.Left(
                            error.toException().firebaseError().mapToFailure()
                        )
                    ).isSuccess
                }
            })
            awaitClose {
                listener?.let { categoryReference.removeEventListener(it) }
            }
        }

    override suspend fun removeCategory(categoryID: String): Either<Failure, Unit> {
        return safeCall {
            categoryWithLanguagesRef.firstOrNull()?.child(categoryID)
                ?.removeValue()
        }
    }

    override suspend fun removeUserCategory(categoryID: String): Either<Failure, Unit> {
        return safeCall {
            userCategoryWithLanguagesRef.firstOrNull()
                ?.child(categoryID)
                ?.removeValue()
        }
    }

    override suspend fun removeUserCard(
        categoryID: String,
        cardID: String
    ): Either<Failure, Unit> {
        return safeCall {
            userCategoryWithLanguagesRef.firstOrNull()
                ?.child(categoryID)
                ?.child(CARD_REF)
                ?.child(cardID)
                ?.removeValue()
        }
    }

    override suspend fun removeUserDatabase() {
        root.child(USER_REF)
            .child(firebaseAuth.currentUser?.uid ?: "no-user-id")
            .removeValue()
    }

    override suspend fun addBook(book: Book): Either<Failure, Unit> {
        return safeCall {
            val learningLanguage = languagesDataStore.getLearningLanguage().firstOrNull()
                ?: throw RuntimeException("learning language is empty")
            val ref = root.child(LIBRARY_REF)
                .child(learningLanguage.code)
                .push()
            val newBook = book.copy(
                id = ref.key ?: "",
                languageCode = learningLanguage.code
            )
            ref.setValue(newBook.toDTO()) { error, ref ->
                println("$ref: ${error?.message}")
            }
        }
    }

    override suspend fun copyExploreCategoryToUserCollection(categoryID: String): Either<Failure, Unit> {
        return safeCall {
            val userCategoryRef = userCategoryWithLanguagesRef.firstOrNull()?.child(categoryID)
            val categoryRef = categoryWithLanguagesRef.firstOrNull()?.child(categoryID)
            userCategoryRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        categoryRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                userCategoryRef.setValue(snapshot.value)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                throw error.toException()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
        }
    }

    override suspend fun copyUserCategoryToExploreCollection(categoryID: String): Either<Failure, Unit> {
        return safeCall {
            val categoryRef = categoryWithLanguagesRef.firstOrNull()?.child(categoryID)
            val cards = getUserCards(categoryID).firstOrNull()?.asRight?.b
                ?.map {
                    it.copy(
                        kind = CardKind.UNDEFINED,
                        nextRepeatTime = Calendar.getInstance().timeInMillis,
                        repeatedCount = 0,
                        learnedPercent = 0f
                    ).toDTO()
                }
            val category = getUserCategories.firstOrNull()?.asRight?.b
                ?.find { it.id == categoryID }
            if (category != null) {
                categoryRef?.setValue(category.toDTO())
                delay(200)
                cards?.forEach {
                    categoryRef?.child(CARD_REF)
                        ?.child(it.id)?.setValue(it)
                }
            }
        }
    }
}
