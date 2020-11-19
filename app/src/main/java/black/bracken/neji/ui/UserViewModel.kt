package black.bracken.neji.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp

class UserViewModel : ViewModel() {

    private val _firebaseApp: MutableLiveData<FirebaseApp?> = MutableLiveData(null)
    val firebaseApp: LiveData<FirebaseApp?> get() = _firebaseApp

    fun setFirebaseApp(app: FirebaseApp) {
        _firebaseApp.postValue(app)
    }

}