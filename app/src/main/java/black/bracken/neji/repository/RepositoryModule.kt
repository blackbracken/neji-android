package black.bracken.neji.repository

import android.content.Context
import androidx.datastore.DataStore
import black.bracken.neji.NejiSecure
import black.bracken.neji.repository.auth.Auth
import black.bracken.neji.repository.auth.AuthImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Provides
    fun provideAuth(
        @ApplicationContext context: Context,
        nejiSecure: DataStore<NejiSecure>
    ): Auth = AuthImpl(context, nejiSecure)

}