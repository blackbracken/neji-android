package black.bracken.neji.model.document

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
@IgnoreExtraProperties
data class Item constructor(
    @get:Exclude override var id: String = "",
    var name: String = "",
    var amount: Int = 0,
    var boxId: String = "",
    var imageUrl: String? = null,
    var itemType: String? = null,
    var comment: String? = null,
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Parcelable, Serializable, HasId