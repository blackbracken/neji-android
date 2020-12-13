package black.bracken.neji.util

sealed class Resource<out T : Any>

class Success<out T : Any>(val value: T) : Resource<T>()
object Loading : Resource<Nothing>()
class Failure(val error: Exception? = null) : Resource<Nothing>()