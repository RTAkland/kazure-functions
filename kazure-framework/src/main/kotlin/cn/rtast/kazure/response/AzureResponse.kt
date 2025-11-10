/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/7/25
 */

package cn.rtast.kazure.response

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.util.Resources
import cn.rtast.kazure.util.toJson
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import java.io.File
import java.io.InputStream

private typealias Headers = Map<String, String>

private fun <T> HttpRequest<T>.builder(headers: Headers, status: HttpStatus): HttpResponseMessage.Builder {
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
    status: HttpStatus = HttpStatus.FOUND,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status)
    .body("Redirecting").header("Location", url).build()

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
): HttpResponse = this.builder(headers, status).body(body).build()

/**
 * Respond json content
 * Auto deserialization to json text
 */
public fun <T, R> HttpRequest<T>.respondJson(
    data: R,
    status: HttpStatus = HttpStatus.OK,
    headers: Map<String, String> = mapOf(),
): HttpResponse = this.builder(headers, status).body(data!!.toJson()).build()