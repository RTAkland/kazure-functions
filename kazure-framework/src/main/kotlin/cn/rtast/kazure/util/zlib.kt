/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/11/29
 */


package cn.rtast.kazure.util

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

public fun ByteArray.zlibCompress(): ByteArray {
    val deflater = Deflater(Deflater.DEFAULT_COMPRESSION)
    deflater.setInput(this)
    deflater.finish()
    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    while (!deflater.finished()) {
        val count = deflater.deflate(buffer)
        outputStream.write(buffer, 0, count)
    }
    deflater.end()
    return outputStream.toByteArray()
}

public fun ByteArray.zlibDecompress(): ByteArray {
    val inflater = Inflater()
    inflater.setInput(this)
    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    try {
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
    } catch (e: Exception) {
        throw IllegalStateException("Decompression error", e)
    } finally {
        inflater.end()
    }
    return outputStream.toByteArray()
}