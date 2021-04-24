package black.bracken.neji.model

import android.os.Parcel
import android.os.Parcelable
import black.bracken.neji.ext.toLocalTimeDate
import black.bracken.neji.firebase.document.ItemEntity
import black.bracken.neji.util.firebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import java.time.LocalDateTime

@Parcelize
data class Item(
    val id: String,
    val name: String,
    val amount: Int,
    val box: Box,
    val imageReference: @WriteWith<StorageReferenceParceler> StorageReference?,
    val itemCategory: ItemCategory?,
    val comment: String?,
    val updatedAt: LocalDateTime
) : Parcelable

object StorageReferenceParceler : Parceler<StorageReference?> {
    // TODO: you shouldn't depend on `firebaseStorage`
    @Suppress("RedundantNullableReturnType")
    override fun create(parcel: Parcel): StorageReference? =
        firebaseStorage.reference.child(parcel.readString() ?: throw IllegalStateException())

    override fun StorageReference?.write(parcel: Parcel, flags: Int) {
        if (this != null) {
            parcel.writeString(path)
        }
    }

}

@Suppress("FunctionName")
suspend fun Item(
    entity: ItemEntity,
    id: String,
    getBox: suspend (boxId: String) -> Box?,
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
        itemCategory = entity.itemCategory?.let { ItemCategory(it) },
        comment = entity.comment,
        updatedAt = entity.updatedAt.toLocalTimeDate()
    )
}
