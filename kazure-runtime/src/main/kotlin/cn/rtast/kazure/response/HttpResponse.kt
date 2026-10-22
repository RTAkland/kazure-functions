/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */

package cn.rtast.kazure.response

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.HttpResponseBuilder
import cn.rtast.kazure.HttpStatus
import cn.rtast.kazure.negotiation.BodyConverterRegistry
import cn.rtast.kazure.negotiation.MediaType
import java.io.File
import java.io.InputStream

private typealias Headers = Map<String, String>


@Suppress("FunctionName")
public fun HttpRequest.__builder(headers: Headers, status: HttpStatus): HttpResponseBuilder {
    var builder = this.createResponseBuilder(status)
    for ((k, v) in headers) builder = builder.header(k, v)
    return builder
}

/**
 * Respond plain text content
 */
public fun HttpRequest.respondText(
    body: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.__builder(headers, status).body(body).build()

/**
 * Respond a bytes content
 */
public fun HttpRequest.respondBytes(
    bytes: ByteArray,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.__builder(headers, status).body(bytes).build()

/**
 * Respond an [InputStream]}
 */
public fun HttpRequest.respondInputStream(
    stream: InputStream,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondBytes(stream.readBytes(), status, headers)

/**
 * Respond a file
 */
public fun HttpRequest.respondFile(
    file: File,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondBytes(file.readBytes(), status, headers)

/**
 * Respond a redirect
 */
public fun HttpRequest.respondRedirect(
    location: String,
    redirectType: RedirectType = RedirectType.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.__builder(headers, redirectType.httpStatus)
    .body("Redirecting").header("Location", location).build()

public fun HttpRequest.respondEmpty(
    status: HttpStatus,
    headers: Map<String, String> = mapOf(),
): HttpResponse = __builder(headers, status).build()

public inline fun <reified T> HttpRequest.respond(
    value: T,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
    mediaType: MediaType? = null,
    registry: BodyConverterRegistry = BodyConverterRegistry.DefaultConverters,
): HttpResponse {
    val ct = mediaType ?: registry.defaultMediaType()
    val bytes = registry.encode(value, ct)
    val mergedHeaders = headers.toMutableMap()
    if (mergedHeaders.keys.none { it.equals("content-type", ignoreCase = true) })
        mergedHeaders["content-type"] = ct.toString()
    return __builder(mergedHeaders, status).body(bytes).build()
}