package black.bracken.neji.ui.setup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import black.bracken.neji.firebase.FirebaseSignInResult
import black.bracken.neji.repository.Auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

class SetupViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val savedState: SavedStateHandle,
    private val auth: Auth
) : ViewModel() {

    private val _verifyResult = MutableLiveData<SignInState>()
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
            val signInResult = auth.signInAndCacheIfSucceed(
                projectId,
                apiKey,
                appId,
                email,
                password
            )
            _verifyResult.postValue(SignInState.Done(signInResult))
        }
    }

    sealed class SignInState {
        data class Done(val result: FirebaseSignInResult) : SignInState()
        object Loading : SignInState()
    }

}