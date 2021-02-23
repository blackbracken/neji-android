package black.bracken.neji.firebase

import com.google.firebase.FirebaseApp

sealed class FirebaseSignInResult {
    data class Success(val firebaseApp: FirebaseApp) : FirebaseSignInResult()
    object InvalidValue : FirebaseSignInResult()
    object MustNotBeBlank : FirebaseSignInResult()
}