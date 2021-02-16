package black.bracken.neji.model

import black.bracken.neji.ext.toLocalTimeDate
import black.bracken.neji.firebase.document.ItemEntity
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime

data class Item(
    val id: String,
    val name: String,
    val amount: Int,
    val box: Box,
    val imageReference: StorageReference?,
    val itemType: String?,
    val comment: String?,
    val updatedAt: LocalDateTime
)

@Suppress("FunctionName")
suspend fun Item(
    entity: ItemEntity,
    id: String,
    getBox: suspend (String) -> Box?,
    getImageReference: suspend (String) -> StorageReference?
): Item? {
    val box = getBox(entity.boxId) ?: return null
    val imageReference = entity.imageUrl?.let { url -> getImageReference(url) }

    return Item(
        id = id,
        name = entity.name,
        amount = entity.amount,
        box = box,
        imageReference = imageReference,
        itemType = entity.itemType,
        comment = entity.comment,
        updatedAt = entity.updatedAt.toLocalTimeDate()
    )
}
