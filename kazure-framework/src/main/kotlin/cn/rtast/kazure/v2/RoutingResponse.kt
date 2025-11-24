/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.v2

import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.HttpResponseBuilder
import cn.rtast.kazure.HttpStatus
import cn.rtast.kazure.resources.Resources
import cn.rtast.kazure.response.RedirectType
import cn.rtast.kazure.util.toJson
import java.io.File
import java.io.InputStream

private typealias Headers = Map<String, String>

private fun <T> HttpRoutingContext<T>.builder(headers: Headers, status: HttpStatus): HttpResponseBuilder {
    var builder = this.request.createResponseBuilder(status)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder
}

/**
 * Respond plain text content
 */
public fun <T> HttpRoutingContext<T>.respondText(
    body: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(body).build()

/**
 * Respond a bytes content
 */
public fun <T> HttpRoutingContext<T>.respondBytes(
    bytes: ByteArray,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(bytes).build()

/**
 * Respond an [InputStream]}
 */
public fun <T> HttpRoutingContext<T>.respondInputStream(
    stream: InputStream,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondBytes(stream.readBytes(), status, headers)

/**
 * Respond a file
 */
public fun <T> HttpRoutingContext<T>.respondFile(
    file: File,
    headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse = respondBytes(file.readBytes(), status, headers)

/**
 * Respond resource from /resources/ folder
 */
public fun <T> HttpRoutingContext<T>.respondResource(
    path: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondBytes(Resources.readBytes(path), status, headers)

/**
 * Respond a redirect
 */
public fun <T> HttpRoutingContext<T>.respondRedirect(
    url: String,
    status: HttpStatus = HttpStatus.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status)
    .body("Redirecting").header("Location", url).build()

/**
 * Respond a redirect
 */
public fun <T> HttpRoutingContext<T>.respondRedirect(
    url: String,
    redirectType: RedirectType = RedirectType.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondRedirect(url, redirectType.httpStatus, headers)

/**
 * Respond any type content
 */
public fun <T> HttpRoutingContext<T>.respond(
    body: Any? = null,
    status: HttpStatus,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(body ?: status.name).build()

/**
 * Respond json content
 * Auto deserialization to json text
 */
public fun <T, R> HttpRoutingContext<T>.respondJson(
    data: R,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(data!!.toJson()).build()