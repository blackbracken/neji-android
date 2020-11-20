package black.bracken.neji.repository

import black.bracken.neji.ext.createFlow
import black.bracken.neji.model.Region
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Singleton

interface FirebaseRepository {

    fun regions(): Flow<List<Region>>

    fun partTypes(): Flow<List<String>>

}

@ExperimentalCoroutinesApi
@Singleton
class FirebaseRepositoryImpl : FirebaseRepository {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance(FirebaseApp.getInstance(Auth.FIREBASE_NAME)).reference
    }

    override fun regions(): Flow<List<Region>> = database.child("region").createFlow(
        onChanged = { snapshot ->
            snapshot.children
                .map { child ->
                    Region(
                        id = UUID.fromString(child.key),
                        name = child.child("name").getValue<String>() ?: "unknown",
                        boxIds = child.child("box").children
                            .filter { it.value == true }
                            .mapNotNull { it.key }
                    )
                }
                .also { regions -> launch { send(regions) } }
        }
    )

    override fun partTypes(): Flow<List<String>> = database.child("parts-type").createFlow(
        onChanged = { snapshot ->
            snapshot.children
                .filter { it.value == true }
                .mapNotNull { child -> child.key }
                .also { partsTypes -> launch { send(partsTypes) } }
        }
    )

}