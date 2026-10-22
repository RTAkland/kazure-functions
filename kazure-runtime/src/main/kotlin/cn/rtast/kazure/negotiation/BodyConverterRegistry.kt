/*
 * Copyright © 2026 rtakland
 * Author: rtakland
 * Date: 2026-10-22
 */

@file:Suppress("PropertyName")

package cn.rtast.kazure.negotiation

import kotlinx.serialization.serializer

public class BodyConverterRegistry {
    private val converters = mutableListOf<BodyConverter>()

    public fun register(converter: BodyConverter): BodyConverterRegistry = apply { converters.add(converter) }

    public fun find(mediaType: MediaType?): BodyConverter? = converters.firstOrNull { it.matches(mediaType) }

    public inline fun <reified T> decode(bytes: ByteArray?, mediaType: MediaType?): T {
        val converter = find(mediaType) ?: throw IllegalArgumentException("No converter for MediaType: $mediaType")
        @Suppress("UNCHECKED_CAST")
        return converter.decode(bytes, serializer<T>(), mediaType!!)
    }

    public inline fun <reified T> encode(value: T, mediaType: MediaType): ByteArray {
        val converter = find(mediaType) ?: throw IllegalArgumentException("No converter for MediaType: $mediaType")
        @Suppress("UNCHECKED_CAST")
        return converter.encode(value, serializer<T>(), mediaType)
    }

    public fun defaultMediaType(): MediaType = converters.firstOrNull()?.mediaTypes?.firstOrNull()
        ?: throw IllegalStateException("No converter registered")

    companion {
        public val DefaultConverters: BodyConverterRegistry = BodyConverterRegistry()
            .register(JsonBodyConverter())
    }
}