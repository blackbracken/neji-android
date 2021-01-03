package black.bracken.neji.model.document

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class Item constructor(
    @get:Exclude val id: String = "",
    var name: String = "",
    var amount: Int = 0,
    var boxId: String = "",
    var itemTypeId: String = "",
    var imageUrl: String? = null,
    var itemType: String? = null,
    var comment: String? = null,
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Serializable