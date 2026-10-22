/*
 * Copyright © 2026 rtakland
 * Author: rtakland
 * Date: 2026-10-22
 */

@file:Suppress("PropertyName")

package cn.rtast.kazure.negotiation

public data class MediaType(val type: String, val subtype: String) {
    public fun matches(other: MediaType): Boolean {
        if (type != other.type && type != "*") return false
        if (subtype == other.subtype) return true
        if (subtype == "*") return true
        if (subtype.startsWith("*+")) return other.subtype.endsWith(subtype.removePrefix("*"))
        return false
    }

    override fun toString(): String = "$type/$subtype"

    companion {
        val ApplicationJson: MediaType = MediaType("application", "json")
        val ApplicationProtoBuf: MediaType = MediaType("application", "protobuf")
        val ApplicationCbor: MediaType = MediaType("application", "cbor")
        val TextPlain: MediaType = MediaType("text", "plain")

        public fun parse(header: String?): MediaType? {
            if (header.isNullOrBlank()) return null
            val base = header.substringBefore(';').trim().lowercase()
            val parts = base.split('/', limit = 2)
            if (parts.size != 2) return null
            return MediaType(parts[0], parts[1])
        }
    }
}