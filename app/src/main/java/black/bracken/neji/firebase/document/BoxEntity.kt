package black.bracken.neji.firebase.document

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class BoxEntity(
    var name: String = "",
    var regionId: String = "",
    var qrCodeText: String? = null,
    var itemTypeAmount: Int = 0,
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Serializable