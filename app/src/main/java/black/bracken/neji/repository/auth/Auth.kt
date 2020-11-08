package black.bracken.neji.repository.auth

import android.content.Context
import androidx.datastore.DataStore
import black.bracken.neji.NejiSecure
import black.bracken.neji.NejiSecure.SignInCache
import black.bracken.neji.model.FirebaseSignInResult
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface Auth {

    val firebaseApp: FirebaseApp?

    suspend fun signInAndCacheIfSucceed(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ): FirebaseSignInResult

    suspend fun signInByCache(signInCache: SignInCache): FirebaseSignInResult

    fun getSignInCaches(): Flow<List<SignInCache>>

}

@Singleton
class AuthImpl @Inject constructor(
    private val context: Context,
    private val nejiSecure: DataStore<NejiSecure>
) : Auth {

    private var _firebaseApp: FirebaseApp? = null
    override val firebaseApp get() = _firebaseApp

    override suspend fun signInAndCacheIfSucceed(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ): FirebaseSignInResult {
        val result = signIn(projectId, apiKey, appId, email, password)
        if (result is FirebaseSignInResult.Success) {
            _firebaseApp = result.firebaseApp

            nejiSecure.updateData { secure ->
                secure.toBuilder()
                    .addSignInCache(
                        SignInCache.newBuilder()
                            .setProjectId(projectId)
                            .setApiKey(apiKey)
                            .setAppId(appId)
                            .setEmail(email)
                            .setPassword(password)
                    )
                    .build()
            }
        }

        return result
    }

    override suspend fun signInByCache(signInCache: SignInCache): FirebaseSignInResult {
        with(signInCache) {
            val result = signIn(projectId, apiKey, appId, email, password)
            if (result is FirebaseSignInResult.Success) {
                _firebaseApp = result.firebaseApp
            }

            return result
        }
    }

    private suspend fun signIn(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ): FirebaseSignInResult {
        if (listOf(projectId, apiKey, appId, email, password).any { it.isBlank() }) {
            return FirebaseSignInResult.MustNotBeBlank
        }

        return suspendCoroutine { continuation ->
            val app = FirebaseApp.initializeApp(
                context,
                FirebaseOptions.Builder()
                    .setApplicationId(appId)
                    .setProjectId(projectId)
                    .setApiKey(apiKey)
                    .build(),
                Math.random().toString()
            )

            FirebaseAuth.getInstance(app).signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(FirebaseSignInResult.Success(app)) }
                .addOnFailureListener { continuation.resume(FirebaseSignInResult.InvalidValue) }
        }
    }


    override fun getSignInCaches(): Flow<List<SignInCache>> =
        nejiSecure.data.map { it.signInCacheList }

}