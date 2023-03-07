package abm.co.domain.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException

suspend fun <T> safeCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    call: suspend () -> T
): Either<Failure, T> =
    withContext(dispatcher) {
        try {
            Either.Right(call.invoke())
        } catch (throwable: Throwable) {
            Either.Left(throwable.mapToFailure())
        }
    }


suspend fun <T : Any?, N> safeCallAndMap(
    call: suspend () -> T,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    mapperFunction: (T) -> N
): Either<Failure, N> = withContext(dispatcher) {
    try {
        Either.Right(call.invoke()).map(mapperFunction)
    } catch (npe: NullPointerException) {
        Either.Left(npe.mapToFailure())
    } catch (jse: JSONException) {
        Either.Left(jse.mapToFailure())
    } catch (uoe: UnsupportedOperationException) {
        Either.Left(uoe.mapToFailure())
    } catch (throwable: Throwable) {
        Either.Left(throwable.mapToFailure())
    }
}
