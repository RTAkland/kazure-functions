/*
 * Copyright © 2026 rtakland
 * Author: rtakland
 * Date: 2026-10-22
 */


package cn.rtast.kazure.negotiation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

public class JsonBodyConverter internal constructor(private val json: Json = kazureInternalJson) : BodyConverter {
    override val mediaTypes: List<MediaType> = listOf(MediaType.ApplicationJson, MediaType("application", "*+json"))
    override fun <T> decode(bytes: ByteArray?, deserializer: KSerializer<T>, mediaType: MediaType): T =
        json.decodeFromString(
            deserializer, bytes?.decodeToString()
                ?: throw IllegalStateException("Request does not have a body")
        )

    override fun <T> encode(value: T, serializer: KSerializer<T>, mediaType: MediaType): ByteArray =
        json.encodeToString(serializer, value).encodeToByteArray()

    companion {
        private val kazureInternalJson: Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            explicitNulls = false
            classDiscriminator = "_json_type_"
            encodeDefaults = true
            coerceInputValues = true
            decodeEnumsCaseInsensitive = true
            isLenient = true
        }
    }
}
