package black.bracken.neji.model

import black.bracken.neji.ext.toLocalTimeDate
import black.bracken.neji.firebase.document.BoxEntity
import java.time.LocalDateTime

data class Box(
    val id: String,
    val name: String,
    val region: Region,
    val updatedAt: LocalDateTime
)

@Suppress("FunctionName")
suspend fun Box(
    entity: BoxEntity,
    id: String,
    getRegion: suspend (String) -> Region?
): Box? {
    val region = getRegion(entity.regionId) ?: return null

    return Box(
        id = id,
        name = entity.name,
        region = region,
        updatedAt = entity.updatedAt.toLocalTimeDate()
    )
}