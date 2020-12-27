package black.bracken.neji.repository

import android.net.Uri
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import black.bracken.neji.model.document.Box
import black.bracken.neji.model.document.Item
import black.bracken.neji.model.document.ItemType
import black.bracken.neji.model.document.Region
import black.bracken.neji.util.PagedValues
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FirebaseRepository {

    suspend fun regions(): PagedValues<Region>

    fun itemTypes(): Flow<Either<Exception, List<ItemType>>>

    suspend fun boxesInRegion(region: Region): List<Box>

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

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance(firebaseApp).reference
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(firebaseApp)
    }

    private val storage: StorageReference by lazy {
        FirebaseStorage.getInstance(firebaseApp).reference
    }

    private val firebaseApp by lazy { FirebaseApp.getInstance(Auth.FIREBASE_NAME) }

    override suspend fun regions(): PagedValues<Region> =
        PagedValues { limitCount, lastVisible ->
            withTimeoutOrNull(5_000) {
                suspendCoroutine { continuation: Continuation<Either<Exception, List<Region>>> ->
                    firestore
                        .collection("regions")
                        .orderBy("updatedAt")
                        .let { query -> if (lastVisible != null) query.startAt(lastVisible) else query }
                        .limit(limitCount.toLong())
                        .get()
                        .addOnSuccessListener { result ->
                            continuation.resume(result.toObjects(Region::class.java).right())
                        }
                        .addOnFailureListener { exception -> continuation.resume(exception.left()) }
                }
            } ?: IllegalStateException("connection timeout").left()
        }

    override fun itemTypes(): Flow<Either<Exception, List<ItemType>>> = callbackFlow {
        val registration = firestore
            .collection("itemTypes")
            .addSnapshotListener { value, error ->
                offer(
                    value?.toObjects(ItemType::class.java)
                        .rightIfNotNull { error ?: IllegalAccessException("something happened") }
                )
            }

        awaitClose { registration.remove() }
    }

    override suspend fun boxesInRegion(region: Region): PagedValues<Box> =
        PagedValues { limitCount, lastVisible ->
            withTimeoutOrNull(5_000) {
                suspendCoroutine { continuation: Continuation<Either<Exception, List<Box>>> ->
                    firestore
                        .collection("boxes")
                        .orderBy("updatedAt")
                        .let { query -> if (lastVisible != null) query.startAt(lastVisible) else query }
                        .limit(limitCount.toLong())
                        .get()
                        .addOnSuccessListener { result ->
                            continuation.resume(result.toObjects(Box::class.java).right())
                        }
                        .addOnFailureListener { exception -> continuation.resume(exception.left()) }
                }
            } ?: java.lang.IllegalStateException("connection timeout").left()
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
        val ref = database.child("item").push()
        val key = ref.key ?: return null

        val imageUrl = suspendCoroutine<String?> { continuation ->
            if (imageUri == null) {
                continuation.resume(null)
                return@suspendCoroutine
            }

            val url = "item/$key/image.jpg"
            storage.child(url)
                .putFile(imageUri)
                .addOnSuccessListener { continuation.resume(url) }
                .addOnFailureListener { continuation.resume(null) }
        }

        val item = Item(
            id = key,
            name = name,
            imageUrl = imageUrl,
            amount = amount,
            itemType = itemType,
            regionId = region.id,
            boxId = box.id,
            comment = comment
        )

        return suspendCoroutine { continuation ->
            database.updateChildren(
                mapOf(
                    "item/$key" to item,
                    "box/${box.id}/itemIds/$key" to true
                )
                // TODO: don't crush error
            ) { error, _ -> continuation.resume(item.takeIf { error != null }) }
        }
    }

}