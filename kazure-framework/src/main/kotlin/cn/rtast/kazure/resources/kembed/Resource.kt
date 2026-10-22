/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/11/29
 */


package cn.rtast.kazure.resources.kembed

import cn.rtast.kazure.util.zlibDecompress
import kotlin.io.encoding.Base64

public class Resource(bSource: List<String>, private val compressed: Boolean = false) {
    private val raw: ByteArray by lazy(LazyThreadSafetyMode.NONE) { bSource.map(Base64::decode).merge() }
    private val decompressed: ByteArray by lazy(LazyThreadSafetyMode.NONE) { if (compressed) raw.zlibDecompress() else raw }

    public fun asByteArray(): ByteArray = decompressed
    public fun asString(): String = decompressed.decodeToString()

    private fun List<ByteArray>.merge(): ByteArray {
        if (size == 1) return this[0]
        val totalSize = sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0
        for (chunk in this) {
            chunk.copyInto(result, offset)
            offset += chunk.size
        }
        return result
    }
}