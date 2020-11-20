package black.bracken.neji.repository

import android.util.Log
import black.bracken.neji.model.Region
import black.bracken.neji.repository.auth.Auth
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Singleton

interface FirebaseRepository {

    fun regions(): Flow<List<Region>>

}

@Singleton
class FirebaseRepositoryImpl : FirebaseRepository {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance(FirebaseApp.getInstance(Auth.FIREBASE_NAME)).reference
    }

    override fun regions(): Flow<List<Region>> = channelFlow {
        Log.i("kero", "hello")
        Log.i("kero", "alive? ${database}")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("kero", "HIHIHIHIHIHI!!!!! ${snapshot.key}")

                val regions = snapshot.children.map { child ->
                    Region(
                        id = UUID.fromString(child.key),
                        name = child.child("name").getValue<String>() ?: "unknown",
                        boxIds = child.child("box").children
                            .filter { it.value == true }
                            .mapNotNull { it.key }
                    )
                }
                launch {
                    Log.i("kero", "sending!!!! regions is ${regions}")
                    send(regions) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("kero", "error!  ${error.toException()}")
                throw error.toException() // TODO: handle
            }
        }

        database.child("region").addValueEventListener(listener)
        awaitClose()

        database.child("region").removeEventListener(listener)
    }

}