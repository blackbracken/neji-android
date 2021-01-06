package black.bracken.neji.repository.source

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import black.bracken.neji.NejiSecure
import black.bracken.neji.security.Crypto
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class NejiSecureSerializer(private val crypto: Crypto) : Serializer<NejiSecure> {

    override val defaultValue: NejiSecure = NejiSecure.newBuilder().build()

    override fun readFrom(input: InputStream): NejiSecure {
        return if (input.available() != 0) {
            try {
                NejiSecure.parseFrom(crypto.decrypt(input))
            } catch (exception: IOException) {
                throw CorruptionException("Cannot read proto", exception)
            }
        } else {
            NejiSecure.newBuilder().build()
        }
    }

    override fun writeTo(t: NejiSecure, output: OutputStream) {
        crypto.encrypt(t.toByteArray(), output)
    }

}