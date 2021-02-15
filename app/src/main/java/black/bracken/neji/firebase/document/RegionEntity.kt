package black.bracken.neji.firebase.document

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class RegionEntity(
    @get:Exclude override var id: String = "",
    var name: String = "",
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Serializable, HasId