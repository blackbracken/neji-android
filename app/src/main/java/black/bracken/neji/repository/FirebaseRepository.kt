package black.bracken.neji.repository

import android.net.Uri
import black.bracken.neji.ext.createSimpleFlow
import black.bracken.neji.model.firebase.Box
import black.bracken.neji.model.firebase.Item
import black.bracken.neji.model.firebase.Region
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FirebaseRepository {

    fun regions(): Flow<List<Region>>

    fun itemTypes(): Flow<List<String>>

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
        FirebaseDatabase.getInstance(FirebaseApp.getInstance(Auth.FIREBASE_NAME)).reference
    }

    private val storage: StorageReference by lazy {
        FirebaseStorage.getInstance(FirebaseApp.getInstance(Auth.FIREBASE_NAME)).reference
    }

    override fun regions(): Flow<List<Region>> = database.child("region").createSimpleFlow(
        onChanged = { snapshot ->
            snapshot.children
                .mapNotNull { child -> child.getValue<Region>()?.copy(id = child.key!!) }
                .also { regions -> launch { send(regions) } }
        }
    )

    override fun itemTypes(): Flow<List<String>> = database.child("item-type").createSimpleFlow(
        onChanged = { snapshot ->
            snapshot.children
                .mapNotNull { child -> child.key }
                .also { itemTypes -> launch { send(itemTypes) } }
        }
    )

    override suspend fun boxesInRegion(region: Region): List<Box> =
        region.box
            .map { boxIdPair -> database.child("box/${boxIdPair.key}") }
            .mapNotNull { child ->
                suspendCoroutine<Box?> { continuation ->
                    child.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            continuation.resume(snapshot.getValue<Box>()?.copy(id = child.key!!))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resume(null)
                        }
                    })
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
                    "box/${box.id}/item/$key" to true
                )
                // TODO: don't crush error
            ) { error, _ -> continuation.resume(item.takeIf { error != null }) }
        }
    }

}