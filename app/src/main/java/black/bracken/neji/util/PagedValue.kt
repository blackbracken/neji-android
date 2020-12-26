package black.bracken.neji.util

import arrow.core.Either
import arrow.core.extensions.list.foldable.isNotEmpty

class PagedValues<out V>(
    private val read: suspend (limitCount: Int, lastVisible: V?) -> Either<Exception, List<V>>
) {
    private var lastVisible: V? = null

    suspend fun readMore(count: Int): Either<Exception, List<V>> {
        val result = read(count, lastVisible)
        result.map { values -> if (values.isNotEmpty()) lastVisible = values.last() }

        return result
    }

}