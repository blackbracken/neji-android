package black.bracken.neji.ui.setup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.model.FirebaseSignInResult
import black.bracken.neji.repository.auth.Auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class SetupViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val savedState: SavedStateHandle,
    private val auth: Auth
) : ViewModel() {

    private val _verifyResult = MutableLiveData<SignInState>(SignInState.Unauthenticated)
    val signInState: LiveData<SignInState> get() = _verifyResult

    fun verifyFirebase(
        projectId: String,
        apiKey: String,
        appId: String,
        email: String,
        password: String
    ) {
        _verifyResult.value = SignInState.Loading

        viewModelScope.launch {
            auth.getSignInCaches()
                .mapLatest { caches ->
                    // TODO: sign in every time
                    if (caches.isEmpty()) {
                        auth.signInAndCacheIfSucceed(projectId, apiKey, appId, email, password)
                    } else {
                        auth.signInByCache(caches.first())
                    }
                }
                .collect { _verifyResult.postValue(SignInState.Done(it)) }
        }
    }

    sealed class SignInState {
        data class Done(val result: FirebaseSignInResult) : SignInState()
        object Loading : SignInState()
        object Unauthenticated : SignInState()
    }

}