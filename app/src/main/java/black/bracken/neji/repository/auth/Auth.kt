package black.bracken.neji.repository.auth

import android.content.Context
import androidx.datastore.DataStore
import black.bracken.neji.NejiSecure
import black.bracken.neji.NejiSecure.SignInCache
import black.bracken.neji.model.FirebaseSignInResult
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface Auth {

    suspend fun signInAndCacheIfSucceed(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ): FirebaseSignInResult

    suspend fun signInByCache(signInCache: SignInCache): FirebaseSignInResult

    suspend fun clearCaches()

    fun getSignInCaches(): Flow<List<SignInCache>>

    companion object {
        const val FIREBASE_NAME = "neji_instance"
    }

}

@Singleton
class AuthImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val nejiSecure: DataStore<NejiSecure>
) : Auth {

    override suspend fun signInAndCacheIfSucceed(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ): FirebaseSignInResult {
        val result = signIn(projectId, apiKey, appId, email, password)
        if (result is FirebaseSignInResult.Success) {
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

    override suspend fun signInByCache(signInCache: SignInCache): FirebaseSignInResult =
        with(signInCache) {
            return signIn(projectId, apiKey, appId, email, password)
        }

    override suspend fun clearCaches() {
        nejiSecure.updateData { secure ->
            secure.toBuilder().build()
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

        val app = FirebaseApp.getApps(context)
            .find { it.name == Auth.FIREBASE_NAME }
            ?: FirebaseApp.initializeApp(
                context,
                FirebaseOptions.Builder()
                    .setApplicationId(appId)
                    .setProjectId(projectId)
                    .setApiKey(apiKey)
                    .build(),
                Auth.FIREBASE_NAME
            )

        return suspendCoroutine { continuation ->
            FirebaseAuth.getInstance(app).signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    continuation.resume(FirebaseSignInResult.Success(app))
                }
                .addOnFailureListener {
                    app.delete()
                    continuation.resume(FirebaseSignInResult.InvalidValue)
                }
        }
    }

    override fun getSignInCaches(): Flow<List<SignInCache>> =
        nejiSecure.data.map { it.signInCacheList }

}