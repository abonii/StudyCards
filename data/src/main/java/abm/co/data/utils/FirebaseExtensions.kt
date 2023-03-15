package abm.co.data.utils

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.base.mapToFailure
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mocklets.pluto.PlutoLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
internal inline fun <reified T : Any> DatabaseReference.asFlow(
    scope: CoroutineScope,
    crossinline converter: (DataSnapshot) -> T?
): Flow<Either<Failure, T?>> {
    val flow = callbackFlow {
        val listener = addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    converter(snapshot).let {
                        trySend(Either.Right(it)).isSuccess
                    }
                } catch (e: Exception) {
                    PlutoLog.e(
                        "DatabaseReference.asFlow",
                        "Error converting snapshot to ${T::class.java.simpleName}: ${e.message}"
                    )
                    trySend(Either.Left(e.firebaseError().mapToFailure())).isSuccess
                }
            }

            override fun onCancelled(error: DatabaseError) {
                PlutoLog.e("DatabaseReference.asFlow", "Error reading data: ${error.message}")
                trySend(Either.Left(error.toException().firebaseError().mapToFailure())).isSuccess
            }
        })

        // When the Flow is cancelled, remove the listener
        channel.invokeOnClose {
            removeEventListener(listener)
        }
    }
    // Cache the result of the first call to the Flow
    return flow.conflate()
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()
        .shareIn(
            scope,
            SharingStarted.Eagerly,
            0
        )
}