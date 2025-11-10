/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */

@file:Suppress("unused")

package cn.rtast.kazure.response

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.util.Resources
import com.microsoft.azure.functions.HttpStatus
import java.io.File
import java.io.InputStream


/**
 * Respond plain text content
 */
public fun <T> HttpRequest<T>.respondText(
    body: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(body)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

/**
 * Respond a bytes content
 */
public fun <T> HttpRequest<T>.respondBytes(
    bytes: ByteArray,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(bytes)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

/**
 * Respond an [InputStream]
 */
public fun <T> HttpRequest<T>.respondInputStream(
    stream: InputStream,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(stream.readBytes())
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

/**
 * Respond a file
 */
public fun <T> HttpRequest<T>.respondFile(
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

/**
 * Respond resource from /resources/ folder
 */
public fun <T> HttpRequest<T>.respondResource(
    path: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse {
    val bytes = Resources.readBytes(path)
    var builder = this.createResponseBuilder(status).body(bytes)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}

/**
 * Respond a redirect
 */
public fun <T> HttpRequest<T>.respondRedirect(
    url: String,
    status: HttpStatus = HttpStatus.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse {
    var builder = this.createResponseBuilder(status).body("Redirecting")
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.header("Location", url).build()
}

/**
 * Respond a redirect
 */
public fun <T> HttpRequest<T>.respondRedirect(
    url: String,
    redirectType: RedirectType = RedirectType.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondRedirect(url, redirectType.httpStatus, headers)

/**
 * Respond any type content
 */
public fun <T, R> HttpRequest<T>.respond(
    body: R,
    status: HttpStatus,
    headers: Map<String, String> = mapOf(),
): HttpResponse {
    var builder = this.createResponseBuilder(status).body(body)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder.build()
}