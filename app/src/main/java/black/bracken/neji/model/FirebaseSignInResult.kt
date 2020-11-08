package black.bracken.neji.model

import com.google.firebase.FirebaseApp

sealed class FirebaseSignInResult {
    data class Success(val firebaseApp: FirebaseApp) : FirebaseSignInResult()
    object InvalidValue : FirebaseSignInResult()
    object MustNotBeBlank : FirebaseSignInResult()
}