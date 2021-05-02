package black.bracken.neji.di

import android.content.Context
import androidx.datastore.core.DataStore
import black.bracken.neji.NejiSecure
import black.bracken.neji.repository.Auth
import black.bracken.neji.repository.AuthImpl
import black.bracken.neji.repository.FirebaseRepository
import black.bracken.neji.repository.FirebaseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Provides
    fun provideFirebaseRepository(): FirebaseRepository = FirebaseRepositoryImpl()

    @Provides
    fun provideAuth(
        @ApplicationContext context: Context,
        nejiSecure: DataStore<NejiSecure>
    ): Auth = AuthImpl(context, nejiSecure)

}