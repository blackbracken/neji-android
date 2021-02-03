package black.bracken.neji.repository

import android.net.Uri
import arrow.core.*
import black.bracken.neji.ext.toObjectsWithId
import black.bracken.neji.model.document.Box
import black.bracken.neji.model.document.Item
import black.bracken.neji.model.document.ItemType
import black.bracken.neji.model.document.Region
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import java.util.*
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FirebaseRepository {

    fun regions(): Flow<Either<Exception, List<Region>>>

    fun itemTypes(): Flow<Either<Exception, List<String>>>

    suspend fun boxesInRegion(regionId: String): Either<Exception, List<Box>>

    suspend fun itemsInBox(boxId: String): Either<Exception, List<Item>>

    suspend fun addItem(
        name: String,
        imageUri: Uri?,
        amount: Int,
        itemType: String,
        boxId: String,
        comment: String?
    ): Either<Exception, Item>

    suspend fun searchItems(query: SearchQuery): Either<Exception, List<Item>>

    data class SearchQuery(
        val name: String?,
        val type: String?,
        val regionName: String?,
        val boxName: String?
    )

}

@ExperimentalCoroutinesApi
@Singleton
class FirebaseRepositoryImpl : FirebaseRepository {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(firebaseApp)
    }

    private val storage: StorageReference by lazy {
        FirebaseStorage.getInstance(firebaseApp).reference
    }

    private val firebaseApp by lazy { FirebaseApp.getInstance(Auth.FIREBASE_NAME) }

    override fun regions(): Flow<Either<Exception, List<Region>>> = callbackFlow {
        val registration = firestore
            .collection("regions")
            .orderBy("updatedAt")
            .addSnapshotListener { value, error ->
                offer(value?.toObjectsWithId<Region>().rightIfNotNull { requireNotNull(error) })
            }

        awaitClose { registration.remove() }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 1)

    override fun itemTypes(): Flow<Either<Exception, List<String>>> = callbackFlow {
        val registration = firestore
            .collection("itemTypes")
            .addSnapshotListener { value, error ->
                offer(
                    value?.toObjects<ItemType>()?.map { it.name }
                        .rightIfNotNull { requireNotNull(error) }
                )
            }

        awaitClose { registration.remove() }
    }

    override suspend fun boxesInRegion(regionId: String): Either<Exception, List<Box>> =
        suspendCoroutine { continuation ->
            firestore
                .collection("boxes")
                .whereEqualTo("regionId", regionId)
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.toObjectsWithId<Box>().right())
                }
                .addOnFailureListener { exception ->
                    continuation.resume(exception.left())
                }
        }

    override suspend fun itemsInBox(boxId: String): Either<Exception, List<Item>> =
        suspendCoroutine { continuation ->
            firestore
                .collection("items")
                .whereIn("boxId", listOf(boxId))
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.toObjectsWithId<Item>().right())
                }
                .addOnFailureListener { exception ->
                    continuation.resume(exception.left())
                }
        }

    override suspend fun addItem(
        name: String,
        imageUri: Uri?,
        amount: Int,
        itemType: String,
        boxId: String,
        comment: String?
    ): Either<Exception, Item> {
        val key = UUID.randomUUID().toString()
        val imageUrl = suspendCoroutine<Either<Exception, String?>> { continuation ->
            if (imageUri == null) {
                continuation.resume(Right(null))
                return@suspendCoroutine
            }

            val url = "items/$key/image.jpg"
            storage.child(url)
                .putFile(imageUri)
                .addOnSuccessListener { continuation.resume(url.right()) }
                .addOnFailureListener { exception -> continuation.resume(exception.left()) }
        }.getOrHandle { exception -> return@addItem exception.left() }

        val item = Item(
            id = key,
            name = name,
            boxId = boxId,
            itemType = itemType,
            imageUrl = imageUrl,
            amount = amount,
            comment = comment
        )

        return suspendCoroutine { continuation ->
            firestore
                .collection("items")
                .document(key)
                .set(item)
                .addOnSuccessListener { continuation.resume(item.right()) }
                .addOnFailureListener { exception -> continuation.resume(exception.left()) }
        }
    }

    override suspend fun searchItems(query: FirebaseRepository.SearchQuery): Either<Exception, List<Item>> {
        return suspendCoroutine { continuation ->
            firestore
                .collection("items")
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(
                        snapshot.toObjects<Item>()
                            .filter { item ->
                                // TODO: suppose if item#name is null
                                // O(N * M)
                                item.name.split(" ", "　").any { it in item.name }
                            }
                            .filter { item -> (query.type == null) || item.itemType == query.type }
                                // TODO: filter with regionId and boxId
                            .right()
                    )
                }
                .addOnFailureListener { continuation.resume(it.left()) }
        }
    }

}