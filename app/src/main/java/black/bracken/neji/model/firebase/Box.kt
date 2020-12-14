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
    var itemIds: Map<String, Boolean> = mapOf(),
    @get:PropertyName(value = "updatedAt") @set:PropertyName(value = "updatedAt") var updatedAtAsEpochSecond: Long = Instant.now().epochSecond
) : Serializable {

    override fun toString() = name

    @get:Exclude
    @set:Exclude
    var updatedAt: Instant by Delegates.observable(Instant.now()) { _, _, new ->
        updatedAtAsEpochSecond = new.epochSecond
    }

    @Exclude
    fun itemIdSet(): Set<String> = itemIds.keys


}