package black.bracken.neji.firebase.document

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class ItemTypeEntity(
    val name: String = ""
) : Serializable