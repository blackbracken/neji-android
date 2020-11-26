package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Box(
    @get:Exclude val id: String = "",
    var name: String = "",
    var region: String = "",
    var item: Map<String, Boolean> = mapOf()
) {

    override fun toString() = name

    @Exclude
    fun itemIdSet(): Set<String> = item.keys

}