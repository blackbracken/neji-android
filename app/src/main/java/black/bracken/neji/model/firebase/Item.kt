package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Item(
    @get:Exclude val id: String = "",
    var name: String = "",
    var amount: Int = 0,
    var imageUrl: String? = null,
    var itemType: String = "",
    var regionId: String = "",
    var boxId: String = "",
    var comment: String? = null
) {

    override fun toString(): String = name

}