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
    @get:PropertyName(value = "updatedAt") @set:PropertyName(value = "updatedAt") var updatedAtAsEpochSecond: Long = Instant.now().epochSecond
) : Serializable {

    @get:Exclude
    @set:Exclude
    var updatedAt: Instant by Delegates.observable(Instant.now()) { _, _, new ->
        updatedAtAsEpochSecond = new.epochSecond
    }

    override fun toString(): String = name

}