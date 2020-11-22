package black.bracken.neji.ext

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

@ExperimentalCoroutinesApi
fun <T> DatabaseReference.createSimpleFlow(
    onChanged: ProducerScope<T>.(DataSnapshot) -> Unit,
    onCancelled: (DatabaseError) -> Unit = { error -> throw error.toException() }
): Flow<T> = channelFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) = onChanged(snapshot)
        override fun onCancelled(error: DatabaseError) = onCancelled(error)
    }
    this@createSimpleFlow.addValueEventListener(listener)
    awaitClose()
    this@createSimpleFlow.removeEventListener(listener)
}