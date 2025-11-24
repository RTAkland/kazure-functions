/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */

@file:Suppress("FuNcTiOnNaMe")

package cn.rtast.kazure.util


import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respond
import cn.rtast.kazure.v2.auth.credential.BasicCredential
import cn.rtast.kazure.v2.auth.credential.BearerCredential
import com.microsoft.azure.functions.HttpStatus
import kotlin.io.encoding.Base64

internal fun <T> __getBasicCredential(req: HttpRequest<T>): BasicCredential? {
    val authorizationHeader = req.headers["authorization"] ?: return null
    val prefix = "Basic "
    if (!authorizationHeader.startsWith(prefix, ignoreCase = true)) return null
    val base64Part = authorizationHeader.removePrefix(prefix).trim()
    val decodedBytes = try {
        Base64.decode(base64Part)
    } catch (_: IllegalArgumentException) {
        return null
    }
    val decoded = decodedBytes.toString(Charsets.UTF_8)
    val colonIndex = decoded.indexOf(':')
    if (colonIndex <= 0 || colonIndex == decoded.length - 1) return null
    val username = decoded.take(colonIndex)
    val password = decoded.substring(colonIndex + 1)
    return BasicCredential(username, password)
}

internal fun <T> __getTokenCredential(req: HttpRequest<T>, scheme: String): String? {
    val authorizationHeader = req.headers["authorization"] ?: return null
    val prefix = "$scheme "
    if (!authorizationHeader.startsWith(prefix, ignoreCase = true)) return null
    val token = authorizationHeader.removePrefix(prefix).trim()
    if (token.isEmpty()) return null
    return token
}

internal fun <T> __getBearerTokenCredential(req: HttpRequest<T>): BearerCredential? {
    val token = __getTokenCredential(req, "Bearer")
    return if (token == null) null else BearerCredential(token)
}

internal fun <T> __unAuthorized(request: HttpRequest<T>): HttpResponse =
    request.respond("UNAUTHORIZED", HttpStatus.UNAUTHORIZED)