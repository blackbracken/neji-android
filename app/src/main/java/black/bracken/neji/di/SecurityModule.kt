package black.bracken.neji.di

import black.bracken.neji.security.AESCipherProvider
import black.bracken.neji.security.CipherProvider
import black.bracken.neji.security.Crypto
import black.bracken.neji.security.CryptoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.security.KeyStore
import javax.inject.Named

@Module(includes = [SecurityModule.Declarations::class])
@InstallIn(ApplicationComponent::class)
object SecurityModule {
    const val KEY_NAME = "Key Name"
    const val KEY_STORE_NAME = "Key Store Name"

    private const val ANDROID_KEY_STORE_TYPE = "AndroidKeyStore"
    private const val NEJI_SECURE_KEY_NAME = "NejiSecureKey"

    @Provides
    fun provideKeyStore(): KeyStore =
        KeyStore.getInstance(ANDROID_KEY_STORE_TYPE).apply { load(null) }

    @Provides
    @Named(KEY_NAME)
    fun providesKeyName(): String =
        NEJI_SECURE_KEY_NAME

    @Provides
    @Named(KEY_STORE_NAME)
    fun providesKeyStoreName(): String =
        ANDROID_KEY_STORE_TYPE

    @Module
    @InstallIn(ApplicationComponent::class)
    interface Declarations {

        @Binds
        fun bindsCipherProvider(impl: AESCipherProvider): CipherProvider

        @Binds
        fun bindsCrypto(impl: CryptoImpl): Crypto
    }

}