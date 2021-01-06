package black.bracken.neji.model.document

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class ItemType(
    val name: String = ""
) : Serializable