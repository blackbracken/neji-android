package black.bracken.neji.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemSearchQuery(
    val byName: String? = null,
    val byCategory: String? = null,
    val byRegionName: String? = null,
    val byBoxName: String? = null
) : Parcelable