package black.bracken.neji.model

import black.bracken.neji.ext.toLocalTimeDate
import black.bracken.neji.firebase.document.RegionEntity
import java.time.LocalDateTime

data class Region(
    val id: String,
    val name: String,
    val updatedAt: LocalDateTime
)

fun Region(entity: RegionEntity, id: String): Region {
    return Region(
        id = id,
        name = entity.name,
        updatedAt = entity.updatedAt.toLocalTimeDate()
    )
}