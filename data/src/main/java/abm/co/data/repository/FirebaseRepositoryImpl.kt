package abm.co.data.repository

import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseRef.CONFIG_REF
import abm.co.data.model.DatabaseRef.SET_OF_CARDS_REF
import abm.co.data.model.DatabaseRef.USER_PROPERTIES_REF
import abm.co.data.model.card.SetOfCardsDTO
import abm.co.data.model.card.toDomain
import abm.co.data.model.user.UserDTO
import abm.co.data.model.user.toDomain
import abm.co.data.utils.asFlow
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.Card
import abm.co.domain.model.CardItem
import abm.co.domain.model.SetOfCards
import abm.co.domain.model.User
import abm.co.domain.repository.ServerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.gson.Gson
import com.mocklets.pluto.PlutoLog
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    @Named(USER_PROPERTIES_REF) private val userProperties: DatabaseReference,
    @Named(SET_OF_CARDS_REF) private val setOfCardsRef: DatabaseReference,
    @Named(CONFIG_REF) private var configRef: DatabaseReference,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    private val gson: Gson,
    private val firebaseAuth: FirebaseAuth
) : ServerRepository {

    override suspend fun getUser(): Flow<Either<Failure, User?>> {
        return userProperties.asFlow(scope = coroutineScope, converter = { snapshot ->
            val map = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any?>?>() {})
            val json = gson.toJson(map)
            val userDTO = gson.fromJson(json, UserDTO::class.java)
            userDTO?.toDomain()
        })
    }

    override suspend fun getSetsOfCards(): Flow<Either<Failure, List<SetOfCards>>> {
        return setOfCardsRef.asFlow(
            scope = coroutineScope,
            converter = { snapshot ->
                val items = ArrayList<SetOfCardsDTO>()
                snapshot.children.map { setOfCards ->
                    try {
                        setOfCards.getValue(SetOfCardsDTO::class.java)
                            ?.let { items.add(it) }
                    } catch (e: DatabaseException) {
                        PlutoLog.e("getSetsOfCards", e.message, e.cause)
                    }
                }
                items.map { it.toDomain() }
            })
    }

    override suspend fun getSetOfCards(id: String): Flow<Either<Failure, CardItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCard(id: String): Flow<Either<Failure, Card>> {
        TODO("Not yet implemented")
    }

    private fun getUserID() = firebaseAuth.currentUser?.uid ?: "no_user_id"

    companion object {
        val className: String = this::class.java.simpleName
    }
}
