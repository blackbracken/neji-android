package black.bracken.neji.util

import black.bracken.neji.repository.Auth
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage

// TODO: this should be smarter

internal val firebaseApp: FirebaseApp
    get() = FirebaseApp.getInstance(Auth.FIREBASE_NAME)

internal val firebaseStorage: FirebaseStorage
    get() = FirebaseStorage.getInstance(firebaseApp)