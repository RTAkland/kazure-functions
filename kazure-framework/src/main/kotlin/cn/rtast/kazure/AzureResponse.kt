/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */

@file:Suppress("unused")

package cn.rtast.kazure

import cn.rtast.kazure.util.Resources
import com.microsoft.azure.functions.HttpStatus
import java.io.File
import java.io.InputStream


public fun HttpRequest.respondText(
    body: String, headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(body)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

public fun HttpRequest.respondBytes(
    bytes: ByteArray,
    headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(bytes)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

public fun HttpRequest.respondInputStream(
    stream: InputStream,
    headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(stream.readBytes())
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

public fun HttpRequest.respondFile(
    file: File,
    headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(file.readBytes())
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

public fun HttpRequest.respondResource(
    path: String,
    headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse {
    val bytes = Resources.readBytes(path)
    var builder = this.createResponseBuilder(status).body(bytes)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}