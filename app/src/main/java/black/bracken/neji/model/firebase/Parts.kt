package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Parts(
    @get:Exclude val id: String = "",
    var name: String = "",
    var amount: Int = 0,
    @get:PropertyName("parts-type") var partsType: String = "",
    @get:PropertyName("region") var regionId: String = "",
    @get:PropertyName("box") var boxId: String = "",
    var comment: String? = null
) {

    override fun toString(): String = name

}