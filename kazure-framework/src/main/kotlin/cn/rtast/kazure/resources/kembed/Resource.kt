/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/11/29
 */


package cn.rtast.kazure.resources.kembed

import cn.rtast.kazure.util.zlibDecompress
import kotlin.io.encoding.Base64

public class Resource(bSource: List<String>, private val compressed: Boolean = false) {
    private val source = bSource.map { Base64.decode(it) }.merge()
    public fun asByteArray(): ByteArray = if (compressed) source.zlibDecompress() else source
    public fun asString(): String = asByteArray().decodeToString()

    private fun List<ByteArray>.merge(): ByteArray {
        val totalSize = this.sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0
        for (chunk in this) {
            chunk.copyInto(result, offset)
            offset += chunk.size
        }
        return result
    }
}