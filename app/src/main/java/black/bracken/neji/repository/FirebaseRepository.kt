package black.bracken.neji.repository

import android.net.Uri
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import black.bracken.neji.model.document.Box
import black.bracken.neji.model.document.Item
import black.bracken.neji.model.document.ItemType
import black.bracken.neji.model.document.Region
import black.bracken.neji.util.PagedValues
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withTimeoutOrNull
import java.util.*
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FirebaseRepository {

    fun itemTypes(): Flow<List<ItemType>?>

    suspend fun regions(): List<Region>?

    suspend fun boxesInRegion(region: Region): PagedValues<Box>

    suspend fun addItem(
        name: String,
        imageUri: Uri?,
        amount: Int,
        itemType: String,
        region: Region,
        box: Box,
        comment: String?
    ): Item?

}

@ExperimentalCoroutinesApi
@Singleton
class FirebaseRepositoryImpl : FirebaseRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(firebaseApp)
    }

    private val storage: StorageReference by lazy {
        FirebaseStorage.getInstance(firebaseApp).reference
    }

    private val firebaseApp by lazy { FirebaseApp.getInstance(Auth.FIREBASE_NAME) }

    override fun itemTypes(): Flow<List<ItemType>?> = callbackFlow {
        val registration = firestore
            .collection("itemTypes")
            .addSnapshotListener { value, _ -> offer(value?.toObjects(ItemType::class.java)) }

        awaitClose { registration.remove() }
    }

    override suspend fun regions(): List<Region>? =
        withTimeoutOrNull(5_000) {
            suspendCoroutine { continuation: Continuation<List<Region>?> ->
                firestore
                    .collection("regions")
                    .orderBy("updatedAt")
                    .get()
                    .addOnSuccessListener { result ->
                        continuation.resume(result.toObjects(Region::class.java))
                    }
                    .addOnFailureListener { continuation.resume(null) }
            }
        }

    override suspend fun boxesInRegion(region: Region): PagedValues<Box> =
        PagedValues { limitCount, lastVisible ->
            withTimeoutOrNull(5_000) {
                suspendCoroutine { continuation: Continuation<List<Box>?> ->
                    firestore
                        .collection("boxes")
                        .orderBy("updatedAt")
                        .let { query -> if (lastVisible != null) query.startAt(lastVisible) else query }
                        .limit(limitCount.toLong())
                        .get()
                        .addOnSuccessListener { result -> continuation.resume(result.toObjects(Box::class.java)) }
                        .addOnFailureListener { continuation.resume(null) }
                }
            }
        }

    override suspend fun addItem(
        name: String,
        imageUri: Uri?,
        amount: Int,
        itemType: String,
        region: Region,
        box: Box,
        comment: String?
    ): Item? {
        val documentId = UUID.randomUUID().toString()
        val imageUrl = suspendCoroutine<Either<Exception, String?>> { continuation ->
            if (imageUri == null) {
                continuation.resume(Either.right(null))
                return@suspendCoroutine
            }

            val url = "item/$documentId/image.jpg"
            storage.child(url)
                .putFile(imageUri)
                .addOnSuccessListener { continuation.resume(url.right()) }
                .addOnFailureListener { exception -> continuation.resume(exception.left()) }
        }.orNull() ?: return null

        val item = Item(
            name = name,
            imageUrl = imageUrl,
            amount = amount,
            itemType = itemType,
            regionId = region.id,
            boxId = box.id,
            comment = comment
        )

        firestore.collection("items").document(documentId).set(item)
        firestore.collection("boxes").document(box.id)
            .update("itemIds", FieldValue.arrayUnion(documentId))

        return item
    }

}