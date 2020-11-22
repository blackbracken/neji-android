package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Parts(
    @Exclude val id: String = "",
    var name: String = "",
    var amount: Int = 0,
    @PropertyName("parts-type") var partsType: String = "",
    @PropertyName("region") var regionId: String = "",
    @PropertyName("box") var boxId: String = "",
    var comment: String? = null
) {

    override fun toString(): String = name

}