package abm.co.domain.base

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Left(a)
    fun <R> right(b: R) = Right(b)

    object Empty : Either<Nothing, Nothing>()
}

inline fun <L, R> Either<L, R>.onSuccess(action: (R) -> Unit): Either<L, R> = onRight(action)

inline fun <L, R> Either<L, R>.onFailure(action: (L) -> Unit): Either<L, R> = onLeft(action)

inline fun <L, R> Either<L, R>.onRight(action: (R) -> Unit): Either<L, R> {
    if (this is Either.Right) action(b)
    return this
}

inline fun <L, R> Either<L, R>.onLeft(action: (L) -> Unit): Either<L, R> {
    if (this is Either.Left) {
        action(a)
    }
    return this
}

inline fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> right(fn(b))
        Either.Empty -> Either.Empty
    }

