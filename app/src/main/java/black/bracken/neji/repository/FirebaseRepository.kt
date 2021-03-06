package black.bracken.neji.repository

import androidx.core.net.toUri
import black.bracken.neji.firebase.document.BoxEntity
import black.bracken.neji.firebase.document.ItemCategoryEntity
import black.bracken.neji.firebase.document.ItemEntity
import black.bracken.neji.firebase.document.RegionEntity
import black.bracken.neji.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FirebaseRepository {

    fun itemCategories(): Flow<List<ItemCategory>?>

    fun regions(): Flow<List<Region>?>

    suspend fun itemCategoriesOnce(): List<ItemCategory>?

    suspend fun region(regionId: String): Region?

    suspend fun box(boxId: String): Box?

    suspend fun findRegionByName(name: String): Region?

    fun boxesInRegion(region: Region): Flow<List<Box>?>

    suspend fun boxesInRegionOnce(region: Region): List<Box>?

    suspend fun itemsInBox(boxId: String): List<Item>?

    suspend fun deleteItemCategory(categoryId: String, categoryName: String): Boolean

    suspend fun addRegion(name: String): Region?

    suspend fun addItemCategory(name: String): ItemCategory?

    suspend fun deleteRegion(regionId: String): Boolean

    suspend fun addBox(name: String, qrCodeText: String?, region: Region): Box?

    suspend fun deleteBox(boxId: String): Boolean

    suspend fun addItem(
        name: String,
        amount: Int,
        itemCategory: ItemCategory?,
        box: Box,
        image: File?,
        comment: String?
    ): Item?

    suspend fun editItem(
        item: Item,
        name: String,
        amount: Int,
        itemCategory: ItemCategory?,
        image: File?,
        box: Box,
        comment: String?
    ): Item?

    suspend fun deleteItem(
        itemId: String
    ): Boolean

    suspend fun updateItemAmount(item: Item, newAmount: Int): Item?

    suspend fun searchItems(query: ItemSearchQuery): List<Item>?

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

    override fun regions(): Flow<List<Region>?> = callbackFlow {
        val registration = firestore
            .collection("regions")
            .orderBy("updatedAt")
            .addSnapshotListener { value, error ->
                val regions = value
                    ?.takeIf { error == null }
                    ?.buildWithId { entity: RegionEntity, id -> Region(entity, id) }

                offer(regions)
            }

        awaitClose { registration.remove() }
    }

    override fun itemCategories(): Flow<List<ItemCategory>?> = callbackFlow {
        val registration = firestore
            .collection("itemCategories")
            .addSnapshotListener { value, error ->
                val itemCategories = value
                    ?.takeIf { error == null }
                    ?.buildWithId { entity: ItemCategoryEntity, id -> ItemCategory(entity, id) }
                offer(itemCategories)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun itemCategoriesOnce(): List<ItemCategory>? =
        suspendCoroutine { continuation ->
            firestore
                .collection("itemCategories")
                .get()
                .addOnSuccessListener { snapshot ->
                    val itemCategories = snapshot
                        .buildWithId { entity: ItemCategoryEntity, id ->
                            ItemCategory(entity, id)
                        }

                    continuation.resume(itemCategories)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }

    override suspend fun region(regionId: String): Region? =
        suspendCoroutine { continuation ->
            firestore
                .collection("regions")
                .document(regionId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val entity = snapshot.toObject<RegionEntity>() ?: run {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }

                    continuation.resume(Region(entity, regionId))
                }
        }

    override suspend fun box(boxId: String): Box? {
        val boxEntity = suspendCoroutine<BoxEntity?> { continuation ->
            firestore
                .collection("boxes")
                .document(boxId)
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.toObject<BoxEntity>())
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } ?: return null

        return Box(boxEntity, boxId) { region(it) }
    }

    override suspend fun findRegionByName(name: String): Region? =
        suspendCoroutine { continuation ->
            firestore
                .collection("regions")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener { task ->
                    continuation.resume(
                        task.result
                            ?.buildWithId { entity: RegionEntity, id -> Region(entity, id) }
                            ?.firstOrNull()
                    )
                }
        }

    override fun boxesInRegion(region: Region): Flow<List<Box>?> = callbackFlow {
        val registration = firestore
            .collection("boxes")
            .whereIn("regionId", listOf(region.id))
            .addSnapshotListener { snapshot, error ->
                val entities = if (snapshot == null || error != null) {
                    null
                } else {
                    snapshot.documents
                        .mapNotNull {
                            it.id to (it.toObject<BoxEntity>() ?: return@mapNotNull null)
                        }
                        .toMap()
                }

                offer(entities)
            }

        awaitClose {
            registration.remove()
        }
    }.mapLatest { map -> map?.mapNotNull { (id, entity) -> Box(entity, id) { region } } }

    override suspend fun boxesInRegionOnce(region: Region): List<Box>? {
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

    override suspend fun itemsInBox(boxId: String): List<Item>? {
        val entityMap = suspendCoroutine<Map<String, ItemEntity>?> { continuation ->
            firestore
                .collection("items")
                .whereIn("boxId", listOf(boxId))
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

        val box = box(boxId) ?: return null

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

    override suspend fun deleteItemCategory(categoryId: String, categoryName: String): Boolean {
        val associatedIds = suspendCoroutine<Set<String>> { continuation ->
            firestore
                .collection("items")
                .whereEqualTo("itemCategories", categoryName)
                .get()
                .addOnCompleteListener { task ->
                    continuation.resume(
                        task.result.documents
                            .map { snapshot -> snapshot.id }
                            .toSet()
                    )
                }
        }

        val removeAssociationResult = associatedIds.all { id ->
            suspendCancellableCoroutine { continuation ->
                firestore
                    .collection("items")
                    .document(id)
                    .update("itemCategories", null)
                    .addOnCompleteListener { task ->
                        continuation.resume(task.isSuccessful)
                    }
            }
        }

        val removeCategoryResult: Boolean = suspendCancellableCoroutine { continuation ->
            firestore
                .collection("itemCategories")
                .document(categoryId)
                .delete()
                .addOnCompleteListener { task ->
                    continuation.resume(task.isSuccessful)
                }
        }

        return removeAssociationResult && removeCategoryResult
    }

    override suspend fun addItem(
        name: String,
        amount: Int,
        itemCategory: ItemCategory?,
        box: Box,
        image: File?,
        comment: String?
    ): Item? {
        val id = UUID.randomUUID().toString()
        val imagePath = imagePathOf(id)

        @Suppress("RedundantReturnLabel")
        val imageReference = if (image != null) {
            updateImage(imagePath, image) ?: return@addItem null
        } else {
            null
        }

        val entity = ItemEntity(
            name = name,
            amount = amount,
            boxId = box.id,
            imageUrl = imagePath,
            itemCategory = itemCategory?.name,
            comment = comment
        )

        suspendCoroutine<Unit?> { continuation ->
            firestore
                .collection("items")
                .document(id)
                .set(entity)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resume(null) }
        } ?: return null

        if (!incrementItemAmountInBox(box.id, 1)) {
            return null
        }

        return Item(
            entity = entity,
            id = id,
            getBox = { box },
            getImageReference = { imageReference }
        )
    }

    override suspend fun editItem(
        item: Item,
        name: String,
        amount: Int,
        itemCategory: ItemCategory?,
        image: File?,
        box: Box,
        comment: String?
    ): Item? {
        val imageReference = if (image != null) {
            @Suppress("RedundantReturnLabel")
            updateImage(imagePathOf(item.id), image) ?: return@editItem null
        } else {
            null
        }

        val newItem = item.copy(
            name = name,
            amount = amount,
            itemCategory = itemCategory,
            imageReference = imageReference,
            box = box,
            comment = comment
        )

        // HACK: consider whether to use reflection to get properties
        val diff = mapOf(
            "name" to newItem.name.takeIf { it != item.name },
            "amount" to newItem.amount.takeIf { it != item.amount },
            "boxId" to newItem.box.takeIf { it != item.box }?.id,
            "imageReference" to newItem.imageReference.takeIf { it != item.imageReference },
            "itemCategory" to newItem.itemCategory?.name.takeIf { it != item.itemCategory?.name },
            "comment" to newItem.comment.takeIf { it != item.comment }
        ).filterValues { it != null }

        if ("boxId" in diff.keys) {
            val oldBox = item.box
            val newBox = newItem.box

            incrementItemAmountInBox(oldBox.id, -1)
            incrementItemAmountInBox(newBox.id, 1)
        }

        return suspendCoroutine { continuation ->
            firestore
                .collection("items")
                .document(item.id)
                .update(diff)
                .addOnSuccessListener { continuation.resume(newItem) }
                .addOnFailureListener { continuation.resume(null) }
        }
    }

    override suspend fun deleteItem(itemId: String): Boolean {
        val boxId = suspendCoroutine<String?> { continuation ->
            firestore
                .collection("items")
                .document(itemId)
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.getString("boxId"))
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } ?: return false

        val imagePath = suspendCoroutine<String?> { continuation ->
            firestore
                .collection("items")
                .document(itemId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val url = snapshot.getString("imageUrl")

                    continuation.resume(url?.let { imagePathOf(it) })
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
        if (imagePath != null) {
            deleteImage(imagePath)
        }

        if (!incrementItemAmountInBox(boxId, -1)) {
            return false
        }

        return suspendCoroutine { continuation ->
            firestore
                .collection("items")
                .document(itemId)
                .delete()
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
        }
    }

    override suspend fun addRegion(name: String): Region? {
        val id = UUID.randomUUID().toString()
        val entity = RegionEntity(name = name)

        val hasSuccessfulAdding = suspendCoroutine<Boolean> { continuation ->
            firestore
                .collection("regions")
                .document(id)
                .set(entity)
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
        }

        return if (hasSuccessfulAdding) {
            Region(entity, id)
        } else {
            null
        }
    }

    override suspend fun addItemCategory(name: String): ItemCategory? {
        val id = UUID.randomUUID().toString()
        val entity = ItemCategoryEntity(name)

        val succeed = suspendCancellableCoroutine<Boolean> { cont ->
            firestore
                .collection("itemCategories")
                .document(id)
                .set(entity)
                .addOnCompleteListener { task -> cont.resume(task.isSuccessful) }
        }

        return ItemCategory(entity, id)
            .takeIf { succeed }
    }

    override suspend fun deleteRegion(regionId: String): Boolean {
        val region = region(regionId) ?: return false
        val boxes = boxesInRegionOnce(region) ?: return false

        boxes.forEach { box -> deleteBox(box.id) }

        return suspendCoroutine { continuation ->
            firestore
                .collection("regions")
                .document(regionId)
                .delete()
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
        }
    }

    override suspend fun addBox(name: String, qrCodeText: String?, region: Region): Box? {
        val id = UUID.randomUUID().toString()
        val entity = BoxEntity(name = name, qrCodeText = qrCodeText, regionId = region.id)

        val hasSuccessfulAdding = suspendCoroutine<Boolean> { continuation ->
            firestore
                .collection("boxes")
                .document(id)
                .set(entity)
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
        }

        suspendCoroutine<Unit?> { continuation ->
            firestore
                .collection("regions")
                .document(region.id)
                .update("boxAmount", FieldValue.increment(1L))
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resume(null) }
        } ?: return null

        return if (hasSuccessfulAdding) {
            Box(entity, id) { region }
        } else {
            null
        }
    }

    override suspend fun deleteBox(boxId: String): Boolean {
        val regionId = suspendCoroutine<String?> { continuation ->
            firestore
                .collection("boxes")
                .document(boxId)
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(snapshot.getString("regionId"))
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } ?: return false

        itemsInBox(boxId)?.forEach { item ->
            deleteItem(item.id)
        }

        suspendCoroutine<Unit?> { continuation ->
            firestore
                .collection("regions")
                .document(regionId)
                .update("boxAmount", FieldValue.increment(-1L))
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resume(null) }
        } ?: return false

        return suspendCoroutine { continuation ->
            firestore
                .collection("boxes")
                .document(boxId)
                .delete()
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
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
        val itemIdAndEntities: List<Pair<String, ItemEntity>> =
            suspendCancellableCoroutine { cont ->
                firestore
                    .collection("items")
                    .let {
                        // filter by category
                        if (query.byCategory != null) {
                            it.whereEqualTo("itemCategories", query.byCategory)
                        } else {
                            it
                        }
                    }
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val result = snapshot
                            .documents
                            .mapNotNull {
                                val key = it.id
                                val value = it.toObject<ItemEntity>() ?: return@mapNotNull null

                                key to value
                            }
                            .toList()

                        cont.resume(result)
                    }
                    .addOnFailureListener { cont.resume(null) }
            } ?: return null

        return itemIdAndEntities
            .mapNotNull { (id, entity) ->
                Item(
                    entity,
                    id,
                    { getBox(it) },
                    { FirebaseStorage.getInstance(firebaseApp).getReference(it) }
                )
            }
            .filter { item ->
                // filter by ItemName which is O(N * M).
                query.byName
                    ?.split(" ", "　")
                    ?.all { it in item.name }
                    ?: true
            }
            .filter { item ->
                // filter by RegionName
                query.byRegionName == null || query.byRegionName == item.box.region.name
            }
            .filter { item ->
                // filter by BoxName
                query.byBoxName == null || query.byBoxName == item.box.name
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

    private suspend fun updateImage(path: String, image: File): StorageReference? {
        deleteImage(path)

        return suspendCoroutine { continuation ->
            storage.child(path)
                .putFile(image.toUri())
                .addOnCompleteListener { task -> continuation.resume(task.result?.storage) }
        }
    }

    private suspend fun deleteImage(path: String): Boolean {
        return suspendCoroutine { continuation ->
            storage.child(path)
                .delete()
                .addOnCompleteListener { task -> continuation.resume(task.isSuccessful) }
        }
    }

    private suspend fun incrementItemAmountInBox(boxId: String, diff: Int): Boolean {
        return suspendCancellableCoroutine { continuation ->
            firestore
                .collection("boxes")
                .document(boxId)
                .update("itemTypeAmount", FieldValue.increment(diff.toLong()))
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { continuation.resume(false) }
        }
    }

    // TODO: 専用の型を用意する
    private fun imagePathOf(id: String) = "items/$id/image.jpg"

    private inline fun <T : Any, reified E : Any> QuerySnapshot?.buildWithId(build: (E, String) -> T): List<T>? {
        return this
            ?.documents
            ?.map { document -> document.id to document.toObject<E>() }
            ?.filter { (_, entity) -> entity != null }
            ?.map { (id, entity) -> build(requireNotNull(entity), id) }
    }

}