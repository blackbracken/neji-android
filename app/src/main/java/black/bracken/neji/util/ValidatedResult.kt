package black.bracken.neji.util

sealed class ValidatedResult<out T> {
    class Success<T>(val value: T) : ValidatedResult<T>()
    class Failure(val error: String? = null) : ValidatedResult<Nothing>()
}