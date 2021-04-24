package black.bracken.neji.firebase.document

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class RegionEntity(
    var name: String = "",
    val boxAmount: Int = 0,
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Serializable