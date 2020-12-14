package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.io.Serializable
import java.time.Instant
import kotlin.properties.Delegates

@IgnoreExtraProperties
data class Box(
    @get:Exclude val id: String = "",
    var name: String = "",
    var regionId: String = "",
    var itemIds: MutableMap<String, Boolean> = HashMap(),
    @get:PropertyName(value = "updatedAt") @set:PropertyName(value = "updatedAt") var updatedAtAsEpochSecond: Long = 0
) : Serializable {

    override fun toString() = name

    @get:Exclude
    @set:Exclude
    var updatedAt: Instant by Delegates.observable(Instant.now()) { _, _, new ->
        updatedAtAsEpochSecond = -1 * new.epochSecond
    }

    @Exclude
    fun itemIdSet(): Set<String> = itemIds.keys

}

fun Box(
    id: String,
    name: String,
    regionId: String,
    itemIds: Map<String, Boolean>
): Box = Box(
    id = id,
    name = name,
    regionId = regionId,
    itemIds = itemIds.toMutableMap(),
    updatedAtAsEpochSecond = 0
).apply {
    updatedAt = Instant.now()
}