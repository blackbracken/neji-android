package black.bracken.neji.model

import com.google.firebase.FirebaseApp

sealed class FirebaseSignInResult {
    data class Success(val firebaseApp: FirebaseApp) : FirebaseSignInResult()
    object InvalidUsernameOrPassword : FirebaseSignInResult()
    object MustNotBeBlank : FirebaseSignInResult()
}