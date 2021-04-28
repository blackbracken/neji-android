package black.bracken.neji.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import black.bracken.neji.firebase.FirebaseSignInResult
import black.bracken.neji.repository.Auth
import com.google.firebase.FirebaseApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val auth: Auth) : ViewModel() {

    private val _firebaseApp: MutableLiveData<FirebaseApp?> = MutableLiveData()
    val firebaseApp: LiveData<FirebaseApp?> get() = _firebaseApp

    fun setFirebaseApp(app: FirebaseApp) {
        _firebaseApp.postValue(app)
    }

    fun signInIfCacheExists() = viewModelScope.launch {
        auth.getSignInCaches().collect { caches ->
            val signInResult = caches.lastOrNull()?.let { cache -> auth.signInByCache(cache) }

            if (signInResult is FirebaseSignInResult.Success) {
                _firebaseApp.postValue(signInResult.firebaseApp)
            } else {
                auth.clearCaches()
                _firebaseApp.postValue(null)
            }
        }
    }

}