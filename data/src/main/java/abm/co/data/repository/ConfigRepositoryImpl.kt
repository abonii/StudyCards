package abm.co.data.repository

import abm.co.data.model.DatabaseRef
import abm.co.data.model.config.ConfigDTO
import abm.co.data.utils.firebaseError
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import abm.co.domain.model.config.Config
import abm.co.domain.repository.ConfigRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mocklets.pluto.PlutoLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    @Named(DatabaseRef.ROOT_REF) private val root: DatabaseReference,
    private val gson: Gson
) : ConfigRepository {

    override fun getConfig(): Flow<Either<Failure, Config>> {
        return callbackFlow {
            val configRef = root.child(DatabaseRef.CONFIG_REF)
            val listener = configRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val map = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any?>?>() {})
                        val json = gson.toJson(map)
                        val configDTO = gson.fromJson(json, ConfigDTO::class.java)
                        val config = configDTO?.toDomain()?: ConfigDTO().toDomain()
                        trySend(Either.Right(config)).isSuccess
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
                listener.let { configRef.removeEventListener(it) }
            }
        }
    }
}
