package black.bracken.neji.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.firebase.FirebaseSignInResult
import black.bracken.neji.repository.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
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