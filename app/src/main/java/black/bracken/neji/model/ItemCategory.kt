package black.bracken.neji.model

import android.os.Parcelable
import black.bracken.neji.firebase.document.ItemCategoryEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemCategory(
    val id: String,
    val name: String
) : Parcelable {

    override fun toString() = name

}

fun ItemCategory(entity: ItemCategoryEntity, id: String): ItemCategory {
    return ItemCategory(
        id = id,
        name = entity.name
    )
}
