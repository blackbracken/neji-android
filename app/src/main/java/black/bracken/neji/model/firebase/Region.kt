package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.io.Serializable
import java.time.Instant
import kotlin.properties.Delegates

@IgnoreExtraProperties
data class Region(
    @get:Exclude val id: String = "",
    var name: String = "",
    var boxIds: MutableMap<String, Boolean> = mutableMapOf(),
    @get:PropertyName(value = "updatedAt") @set:PropertyName(value = "updatedAt") var updatedAtAsEpochSecond: Long = Instant.now().epochSecond
) : Serializable {

    override fun toString(): String = name

    @get:Exclude
    @set:Exclude
    var updatedAt: Instant by Delegates.observable(Instant.now()) { _, _, new ->
        updatedAtAsEpochSecond = new.epochSecond
    }

    @Exclude
    fun boxIdSet(): Set<String> = boxIds.keys

}