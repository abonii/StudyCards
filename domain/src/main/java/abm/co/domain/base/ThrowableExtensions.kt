package abm.co.domain.base

import abm.co.domain.exception.ClientSideException
import abm.co.domain.exception.InternalServerException
import abm.co.domain.exception.InvalidTokenException
import abm.co.domain.exception.NetworkException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException

/**
 * we should ignore [CancellationException] that thrown when request cancelled
 */
fun Throwable.mapToFailure(): Failure {
    return when (this) {
        is InvalidTokenException -> Failure.FailureInvalidToken(expectedMessage)
        is InternalServerException -> Failure.FailureInternalServer(expectedMessage)
        is ClientSideException -> Failure.FailureSnackbar(expectedMessage)
        is NetworkException -> Failure.FailureNetwork
        is SocketTimeoutException -> Failure.FailureTimeout
        is CancellationException -> Failure.Ignorable
        else -> Failure.FailureAlert(this.message)
    }
}
