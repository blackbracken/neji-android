package black.bracken.neji.util

sealed class Resource<out T : Any>

class Success<out T : Any>(val value: T) : Resource<T>()
object Loading : Resource<Nothing>()
class Failure<E : Exception>(val error: E? = null) : Resource<Nothing>()