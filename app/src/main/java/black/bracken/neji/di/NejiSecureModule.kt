package black.bracken.neji.di

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import black.bracken.neji.NejiSecure
import black.bracken.neji.repository.source.NejiSecureSerializer
import black.bracken.neji.security.Crypto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object NejiSecureModule {

    @Provides
    fun providesNejiSecure(
        @ApplicationContext context: Context,
        crypto: Crypto
    ): DataStore<NejiSecure> = context.createDataStore(
        fileName = "NejiSecure.pb",
        serializer = NejiSecureSerializer(crypto)
    )

}