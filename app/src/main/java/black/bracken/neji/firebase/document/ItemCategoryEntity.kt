package black.bracken.neji.firebase.document

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class ItemCategoryEntity(
    val name: String = ""
) : Serializable