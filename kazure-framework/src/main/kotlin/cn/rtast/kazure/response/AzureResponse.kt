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
import cn.rtast.kazure.resources.Resources
import cn.rtast.kazure.util.toJson
import java.io.File
import java.io.InputStream

private typealias Headers = Map<String, String>

private fun <T> HttpRequest<T>.builder(headers: Headers, status: HttpStatus): HttpResponseBuilder {
    var builder = this.createResponseBuilder(status)
    for ((k, v) in headers) {
        builder = builder.header(k, v)
    }
    return builder
}

/**
 * Respond plain text content
 */
public fun <T> HttpRequest<T>.respondText(
    body: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(body).build()

/**
 * Respond a bytes content
 */
public fun <T> HttpRequest<T>.respondBytes(
    bytes: ByteArray,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(bytes).build()

/**
 * Respond an [InputStream]}
 */
public fun <T> HttpRequest<T>.respondInputStream(
    stream: InputStream,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondBytes(stream.readBytes(), status, headers)

/**
 * Respond a file
 */
public fun <T> HttpRequest<T>.respondFile(
    file: File,
    headers: Map<String, String> = mapOf(),
    status: HttpStatus = HttpStatus.OK,
): HttpResponse = respondBytes(file.readBytes(), status, headers)

/**
 * Respond resource from /resources/ folder
 */
public fun <T> HttpRequest<T>.respondResource(
    path: String,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = respondBytes(Resources.readBytes(path), status, headers)

/**
 * Respond a redirect
 */
public fun <T> HttpRequest<T>.respondRedirect(
    url: String,
    redirectType: RedirectType = RedirectType.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, redirectType.httpStatus)
    .body("Redirecting").header("Location", url).build()

/**
 * Respond any type content
 */
public fun <T> HttpRequest<T>.respond(
    body: Any? = null,
    status: HttpStatus,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(body ?: status.name).build()

/**
 * Respond json content
 * Auto deserialization to json text
 */
public fun <T, R> HttpRequest<T>.respondJson(
    data: R,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(data!!.toJson()).build()