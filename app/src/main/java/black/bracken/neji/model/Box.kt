package black.bracken.neji.model

import android.os.Parcelable
import black.bracken.neji.ext.toLocalTimeDate
import black.bracken.neji.firebase.document.BoxEntity
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Box(
    val id: String,
    val name: String,
    val region: Region,
    val qrCodeText: String?,
    val itemAmount: Int,
    val updatedAt: LocalDateTime
) : Parcelable {

    override fun toString(): String = name

}

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
        qrCodeText = entity.qrCodeText,
        itemAmount = entity.itemAmount,
        updatedAt = entity.updatedAt.toLocalTimeDate()
    )
}