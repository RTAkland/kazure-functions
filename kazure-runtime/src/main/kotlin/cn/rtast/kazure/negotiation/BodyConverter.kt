/*
 * Copyright © 2026 rtakland
 * Author: rtakland
 * Date: 2026-10-22
 */


package cn.rtast.kazure.negotiation

import kotlinx.serialization.KSerializer

public interface BodyConverter {
    public val mediaTypes: List<MediaType>

    public fun matches(mediaType: MediaType?): Boolean = mediaType != null && mediaTypes.any { it.matches(mediaType) }

    public fun <T> decode(bytes: ByteArray?, deserializer: KSerializer<T>, mediaType: MediaType): T

    public fun <T> encode(value: T, serializer: KSerializer<T>, mediaType: MediaType): ByteArray
}