package black.bracken.neji.util

sealed class Resource<out T : Any> {
    abstract fun <R : Any> map(transform: (T) -> R): Resource<R>
}

class Success<out T : Any>(val value: T) : Resource<T>() {
    override fun <R : Any> map(transform: (T) -> R): Resource<R> = Success(transform(value))
}

object Loading : Resource<Nothing>() {
    override fun <R : Any> map(transform: (Nothing) -> R): Resource<R> = Loading
}

class Failure(val error: Exception? = null) : Resource<Nothing>() {
    override fun <R : Any> map(transform: (Nothing) -> R): Resource<R> = Failure(error)
}