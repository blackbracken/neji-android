package black.bracken.neji.repository

import black.bracken.neji.ext.createSimpleFlow
import black.bracken.neji.model.firebase.Box
import black.bracken.neji.model.firebase.Parts
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

    fun partTypes(): Flow<List<String>>

    suspend fun boxesInRegion(region: Region): List<Box>

    suspend fun addParts(
        name: String,
        amount: Int,
        partsType: String,
        region: Region,
        box: Box,
        comment: String?
    ): Parts?

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

    override fun partTypes(): Flow<List<String>> = database.child("parts-type").createSimpleFlow(
        onChanged = { snapshot ->
            snapshot.children
                .mapNotNull { child -> child.key }
                .also { partsTypes -> launch { send(partsTypes) } }
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

    override suspend fun addParts(
        name: String,
        amount: Int,
        partsType: String,
        region: Region,
        box: Box,
        comment: String?
    ): Parts? {
        val ref = database.child("parts").push()
        val key = ref.key ?: return null
        val parts = Parts(
            id = key,
            name = name,
            amount = amount,
            partsType = partsType,
            regionId = region.id,
            boxId = box.id,
            comment = comment
        )

        return suspendCoroutine { continuation ->
            database.updateChildren(
                mapOf(
                    "parts/$key" to parts,
                    "box/${box.id}/parts/$key" to true
                )
                // TODO: don't crush error
            ) { error, _ -> continuation.resume(parts.takeIf { error != null }) }
        }
    }

}