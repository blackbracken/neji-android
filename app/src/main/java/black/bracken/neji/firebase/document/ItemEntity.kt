package black.bracken.neji.firebase.document

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
@IgnoreExtraProperties
data class ItemEntity constructor(
    var name: String = "",
    var amount: Int = 0,
    var boxId: String = "",
    // TODO: URLではなくてURI、何ならPathで良い
    var imageUrl: String? = null,
    var itemCategory: String? = null,
    var comment: String? = null,
    @JvmField @ServerTimestamp var updatedAt: Date = Date()
) : Parcelable, Serializable