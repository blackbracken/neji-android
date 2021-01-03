package black.bracken.neji.ext

import black.bracken.neji.model.document.HasId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

inline fun <reified T : HasId> QuerySnapshot.toObjectsWithId(): List<T> =
    this.documents.mapNotNull { it.toObjectWithId<T>() }

inline fun <reified T : HasId> DocumentSnapshot.toObjectWithId(): T? =
    toObject<T>()?.also { converted -> converted.id = this.id }