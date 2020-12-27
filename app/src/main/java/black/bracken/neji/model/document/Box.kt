package black.bracken.neji.model.document

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class Box(
    @get:Exclude val id: String = "",
    var name: String = "",
    var regionId: String = "",
    var itemIds: List<String> = listOf(),
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Serializable