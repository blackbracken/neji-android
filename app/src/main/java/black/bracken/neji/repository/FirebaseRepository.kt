package black.bracken.neji.repository

import android.net.Uri
import arrow.core.*
import black.bracken.neji.firebase.document.BoxEntity
import black.bracken.neji.firebase.document.ItemEntity
import black.bracken.neji.firebase.document.ItemTypeEntity
import black.bracken.neji.firebase.document.RegionEntity
import black.bracken.neji.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FirebaseRepository {

    fun itemTypes(): Flow<List<ItemType>?>

    fun regions(): Flow<List<Region>?>

    suspend fun boxesInRegion(region: Region): List<Box>?

    suspend fun itemsInBox(box: Box): List<Item>?

    suspend fun addItem(
        name: String,
        amount: Int,
        itemType: ItemType,
        box: Box,
        imageUri: Uri?,
        comment: String?
    ): Item?

    suspend fun updateItemAmount(item: Item, newAmount: Int): Item?

    suspend fun searchItems(query: ItemSearchQuery): List<Item>?

    fun _searchItems(query: ItemSearchQuery): Flow<List<Item>?>

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

    override fun regions(): Flow<List<Region>?> = callbackFlow {
        val registration = firestore
            .collection("regions")
            .orderBy("updatedAt")
            .addSnapshotListener { value, error ->
                val regions = value
                    ?.takeIf { error == null }
                    ?.buildWithId { id, entity: RegionEntity -> Region(entity, id) }

                offer(regions)
            }

        awaitClose { registration.remove() }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 1)

    override fun itemTypes(): Flow<List<ItemType>?> = callbackFlow {
        val registration = firestore
            .collection("itemTypes")
            .addSnapshotListener { value, error ->
                val itemTypes = value
                    ?.takeIf { error == null }
                    ?.buildWithId { _, entity: ItemTypeEntity -> ItemType(entity) }
                offer(itemTypes)
            }

        awaitClose { registration.remove() }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 1)

    override suspend fun boxesInRegion(region: Region): List<Box>? {
        val entityMap = suspendCoroutine<Map<String, BoxEntity>?> { continuation ->
            firestore
                .collection("boxes")
                .whereIn("regionId", listOf(region.id))
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(
                        snapshot.documents
                            .mapNotNull {
                                it.id to (it.toObject<BoxEntity>() ?: return@mapNotNull null)
                            }
                            .toMap()
                    )
                }
                .addOnFailureListener { continuation.resume(null) }
        }

        return entityMap?.mapNotNull { (id, entity) -> Box(entity, id) { region } }
    }

    override suspend fun itemsInBox(box: Box): List<Item>? {
        val entityMap = suspendCoroutine<Map<String, ItemEntity>?> { continuation ->
            firestore
                .collection("items")
                .whereIn("boxId", listOf(box.id))
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(
                        snapshot.documents
                            .mapNotNull {
                                it.id to (it.toObject<ItemEntity>() ?: return@mapNotNull null)
                            }
                            .toMap()
                    )
                }
                .addOnFailureListener { continuation.resume(null) }
        }

        return entityMap?.mapNotNull { (id, entity) ->
            Item(
                entity = entity,
                id = id,
                getBox = { box },
                getImageReference = { url ->
                    url.run(FirebaseStorage.getInstance(firebaseApp)::getReference)
                }
            )
        }
    }

    override suspend fun addItem(
        name: String,
        amount: Int,
        itemType: ItemType,
        box: Box,
        imageUri: Uri?,
        comment: String?
    ): Item? {
        val id = UUID.randomUUID().toString()
        val imagePath = suspendCoroutine<Pair<String, StorageReference>?> { continuation ->
            if (imageUri == null) {
                continuation.resume(null)
            } else {
                val path = "items/$id/image.jpg"
                storage.child(path)
                    .putFile(imageUri)
                    .addOnCompleteListener { task ->
                        continuation.resume(
                            task.result?.let { result -> path to result.storage }
                        )
                    }
            }
        }

        val entity = ItemEntity(
            name = name,
            amount = amount,
            boxId = box.id,
            imageUrl = imagePath?.first,
            itemType = itemType.name,
            comment = comment
        )

        val hasSuccessfulAdding = suspendCoroutine<Boolean> { continuation ->
            firestore
                .collection("items")
                .document(id)
                .set(entity)
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
        }

        return if (hasSuccessfulAdding) {
            Item(
                entity = entity,
                id = id,
                getBox = { box },
                getImageReference = { imagePath?.second }
            )
        } else {
            null
        }
    }

    override suspend fun updateItemAmount(item: Item, newAmount: Int): Item? {
        return suspendCoroutine { continuation ->
            firestore
                .collection("items")
                .document(item.id)
                .update("amount", newAmount)
                .addOnSuccessListener { continuation.resume(item.copy(amount = newAmount)) }
                .addOnFailureListener { continuation.resume(null) }
        }
    }

    override suspend fun searchItems(query: ItemSearchQuery): List<Item>? {
        val entityMap = suspendCoroutine<Map<String, ItemEntity>?> { continuation ->
            firestore
                .collection("items")
                .let {
                    // filter by itemType
                    if (query.byType != null) {
                        it.whereEqualTo("itemType", query.byType)
                    } else {
                        it
                    }
                }
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(
                        snapshot
                            .documents
                            .mapNotNull {
                                val key = it.id
                                val value = it.toObject<ItemEntity>() ?: return@mapNotNull null
                                key to value
                            }
                            .filter { (_, entity) ->
                                // filter by ItemName, this order is O(N * M).
                                query.byName.split(" ", "　").any { it in entity.name }
                            }
                            // TODO: filter with regionId and boxId
                            .toMap()
                    )
                }
                .addOnFailureListener { continuation.resume(null) }
        }

        return entityMap?.mapNotNull { (id, entity) ->
            Item(
                entity = entity,
                id = id,
                getBox = { getBox(entity.boxId) },
                getImageReference = { imagePath ->
                    FirebaseStorage.getInstance(firebaseApp).getReference(imagePath)
                }
            )
        }
    }

    override fun _searchItems(query: ItemSearchQuery): Flow<List<Item>?> =
        callbackFlow {
            val registration = firestore
                .collection("items")
                .let {
                    // filter by itemType
                    if (query.byType != null) {
                        it.whereEqualTo("itemType", query.byType)
                    } else {
                        it
                    }
                }
                .addSnapshotListener { snapshot, error ->
                    val result =
                        if (snapshot == null || error != null) {
                            null
                        } else {
                            snapshot
                                .documents
                                .mapNotNull {
                                    val key = it.id
                                    val value = it.toObject<ItemEntity>() ?: return@mapNotNull null
                                    key to value
                                }
                                .filter { (_, entity) ->
                                    // filter by ItemName, this order is O(N * M).
                                    query.byName.split(" ", "　").any { it in entity.name }
                                }
                                // TODO: filter with regionId and boxId
                                .toMap()
                        }

                    offer(result)
                }

            awaitClose { registration.remove() }
        }
            .map { entities ->
                entities?.mapNotNull { (id, entity) ->
                    Item(
                        entity = entity,
                        id = id,
                        getBox = { getBox(entity.boxId) },
                        getImageReference = { imagePath ->
                            FirebaseStorage.getInstance(firebaseApp).getReference(imagePath)
                        }
                    )
                }
            }

    private suspend fun getRegion(id: String): Region? = suspendCoroutine { continuation ->
        firestore
            .collection("regions")
            .document(id)
            .get()
            .addOnSuccessListener { snapshot ->
                val region = snapshot.toObject<RegionEntity>()
                    ?.let { entity ->
                        Region(
                            entity = entity,
                            id = id
                        )
                    }
                continuation.resume(region)
            }
            .addOnFailureListener { continuation.resume(null) }
    }

    private suspend fun getBox(id: String): Box? {
        val boxEntity = suspendCoroutine<BoxEntity?> { continuation ->
            firestore
                .collection("boxes")
                .document(id)
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.toObject<BoxEntity>())
                }
                .addOnFailureListener { continuation.resume(null) }
        } ?: return null

        return Box(
            entity = boxEntity,
            id = id,
            getRegion = { getRegion(boxEntity.regionId) }
        )
    }

    private inline fun <T : Any, reified E : Any> QuerySnapshot?.buildWithId(build: (String, E) -> T): List<T>? {
        return this
            ?.documents
            ?.map { document -> document.id to document.toObject<E>() }
            ?.filter { (_, entity) -> entity != null }
            ?.map { (id, entity) -> build(id, requireNotNull(entity)) }
    }

}