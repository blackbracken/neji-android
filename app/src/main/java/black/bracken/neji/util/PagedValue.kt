package black.bracken.neji.util

class PagedValues<out V>(
    private val read: suspend (limitCount: Int, lastVisible: V?) -> List<V>?
) {
    private var lastVisible: V? = null

    suspend fun readMore(count: Int): List<V>? {
        val result = read(count, lastVisible)
        if (result != null) {
            lastVisible = result.lastOrNull()
        }

        return result
    }

}