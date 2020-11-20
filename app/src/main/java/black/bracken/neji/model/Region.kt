package black.bracken.neji.model

import java.util.*

data class Region(
    val id: UUID,
    val name: String,
    val boxIds: List<String> // TODO: re-model
)