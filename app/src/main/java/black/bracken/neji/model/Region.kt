package black.bracken.neji.model

import android.os.Parcelable
import black.bracken.neji.ext.toLocalTimeDate
import black.bracken.neji.firebase.document.RegionEntity
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Region(
    val id: String,
    val name: String,
    val boxTypeAmount: Int,
    val updatedAt: LocalDateTime
) : Parcelable {

    override fun toString() = name

}

fun Region(entity: RegionEntity, id: String): Region {
    return Region(
        id = id,
        name = entity.name,
        boxTypeAmount = entity.boxTypeAmount,
        updatedAt = entity.updatedAt.toLocalTimeDate()
    )
}