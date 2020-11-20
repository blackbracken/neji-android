package black.bracken.neji.repository

import black.bracken.neji.model.Region
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Singleton

interface FirebaseRepository {

    fun regions(): Flow<List<Region>>

}

@ExperimentalCoroutinesApi
@Singleton
class FirebaseRepositoryImpl : FirebaseRepository {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance(FirebaseApp.getInstance(Auth.FIREBASE_NAME)).reference
    }

    override fun regions(): Flow<List<Region>> = channelFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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

            override fun onCancelled(error: DatabaseError) {
                throw error.toException() // TODO: handle
            }
        }
        database.child("region").addValueEventListener(listener)
        awaitClose()

        database.child("region").removeEventListener(listener)
    }

}