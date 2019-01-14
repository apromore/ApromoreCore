package cs.ut.exceptions

/**
 * Sealed class that represents result of an action
 */
sealed class Either<out L, out R>

/**
 * Left part of the result, containing on the left side if it occurs and Nothing on the right side.
 *
 * @see Nothing
 */
data class Left<out L>(val error: L) : Either<L, Nothing>()

/**
 * Right part of the result containing value on the right side and Nothing on the left side
 *
 * @see Nothing
 */
data class Right<out R>(val result: R) : Either<Nothing, R>()

/**
 * Perform an operation that returns an Either
 *
 * @see Either
 */
inline fun <R> perform(f : () -> R) : Either<Exception, R> = try {
    Right(f())
} catch (e: Exception) {
    Left(e)
}