/*
 * Copyright © 2026 rtakland
 * Author: rtakland
 * Date: 2026-10-22
 */


@file:OptIn(ExperimentalSerializationApi::class)

package cn.rtast.kazure.negotiation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf

public class ProtobufBodyConverter internal constructor(
    private val protoBuf: ProtoBuf = kazureInternalProtoBuf,
) : BodyConverter {

    override val mediaTypes: List<MediaType> = listOf(
        MediaType("application", "protobuf"),
        MediaType("application", "x-protobuf"),
    )

    override fun <T> decode(bytes: ByteArray?, deserializer: KSerializer<T>, mediaType: MediaType): T =
        protoBuf.decodeFromByteArray(
            deserializer, bytes ?: throw IllegalStateException("Request does not have a body")
        )

    override fun <T> encode(value: T, serializer: KSerializer<T>, mediaType: MediaType): ByteArray =
        protoBuf.encodeToByteArray(serializer, value)

    companion {
        private val kazureInternalProtoBuf: ProtoBuf = ProtoBuf {
            encodeDefaults = true
        }
    }
}