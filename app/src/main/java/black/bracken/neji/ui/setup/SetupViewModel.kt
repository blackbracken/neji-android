package black.bracken.neji.ui.setup

import android.content.Context
import androidx.datastore.DataStore
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.NejiSecure
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SetupViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val savedState: SavedStateHandle,
    private val nejiSecure: DataStore<NejiSecure>
) : ViewModel() {

    private val _verifyResult = MutableLiveData<VerifyResult>()
    val verifyResult: LiveData<VerifyResult> get() = _verifyResult

    fun verifyFirebase(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ) {
        val app = try {
            FirebaseApp.initializeApp(
                context,
                FirebaseOptions.Builder()
                    .setApplicationId(appId)
                    .setProjectId(projectId)
                    .setApiKey(apiKey)
                    .build(),
                Math.random().toString()
            )
        } catch (ex: IllegalArgumentException) {
            _verifyResult.value = VerifyResult.Failure(ex.message.toString())
            return
        }

        viewModelScope.launch {
            _verifyResult.postValue(
                withTimeoutOrNull(50_000) {
                    suspendCoroutine<VerifyResult> { continuation ->
                        val auth = FirebaseAuth.getInstance(app)

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { continuation.resume(VerifyResult.Success) }
                            .addOnFailureListener { x ->
                                continuation.resume(VerifyResult.Failure("Invalid email or username"))
                                x.printStackTrace()
                            }
                    }
                } ?: VerifyResult.Timeout
            )
        }
    }

    sealed class VerifyResult {
        object Success : VerifyResult()
        data class Failure(val message: String) : VerifyResult()
        object Timeout : VerifyResult()
    }

}