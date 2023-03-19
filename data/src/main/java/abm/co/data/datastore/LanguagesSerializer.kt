package abm.co.data.datastore

import abm.co.data.model.user.LanguageDTO
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Singleton
class LanguagesSerializer @Inject constructor() : Serializer<LanguageDTO?> {

    override val defaultValue: LanguageDTO? = null

    override suspend fun readFrom(input: InputStream): LanguageDTO? {
        return try {
            Json.decodeFromString(
                deserializer = LanguageDTO.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun writeTo(t: LanguageDTO?, output: OutputStream) {
        if (t == null) {
            output.write(byteArrayOf())
            return
        }
        output.write(
            Json.encodeToString(
                serializer = LanguageDTO.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
