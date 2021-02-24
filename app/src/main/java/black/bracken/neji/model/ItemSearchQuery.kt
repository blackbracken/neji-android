package black.bracken.neji.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemSearchQuery(
    val byName: String,
    val byType: String?,
    val byRegionName: String?,
    val byBoxName: String?
) : Parcelable