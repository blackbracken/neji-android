package black.bracken.neji.model.firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Region(
    @get:Exclude val id: String = "",
    var name: String = "",
    var boxIds: Map<String, Boolean> = mapOf()
) {

    override fun toString(): String = name

    @Exclude
    fun boxIdSet(): Set<String> = boxIds.keys

}