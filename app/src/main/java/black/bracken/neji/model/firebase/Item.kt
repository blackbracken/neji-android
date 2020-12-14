package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.io.Serializable
import java.time.Instant
import kotlin.properties.Delegates

@IgnoreExtraProperties
data class Item(
    @get:Exclude val id: String = "",
    var name: String = "",
    var amount: Int = 0,
    var imageUrl: String? = null,
    var itemType: String = "",
    var regionId: String = "",
    var boxId: String = "",
    var comment: String? = null,
    @get:PropertyName(value = "updatedAt") @set:PropertyName(value = "updatedAt") var updatedAtAsEpochSecond: Long = 0
) : Serializable {

    @get:Exclude
    @set:Exclude
    var updatedAt: Instant by Delegates.observable(Instant.now()) { _, _, new ->
        updatedAtAsEpochSecond = -1 * new.epochSecond
    }

    override fun toString(): String = name

}

fun Item(
    id: String,
    name: String,
    amount: Int,
    imageUrl: String? = null,
    itemType: String,
    regionId: String,
    boxId: String,
    comment: String? = null
): Item = Item(
    id = id,
    name = name,
    amount = amount,
    imageUrl = imageUrl,
    itemType = itemType,
    regionId = regionId,
    boxId = boxId,
    comment = comment,
    updatedAtAsEpochSecond = 0
).apply {
    updatedAt = Instant.now()
}