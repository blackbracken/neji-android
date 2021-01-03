package black.bracken.neji.model.document

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class Box @Deprecated(
    message = "this default constructor should be only used for firebase-sdk"
) constructor(
    @get:Exclude val id: String = "",
    var name: String = "",
    var regionId: String = "",
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Serializable