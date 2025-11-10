/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */

@file:Suppress("FunctionName")

package cn.rtast.kazure.auth.provider.func

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.credentials.BearerCredential
import cn.rtast.kazure.auth.credentials.JwtCredential
import cn.rtast.kazure.response.respond
import com.microsoft.azure.functions.HttpStatus
import kotlin.io.encoding.Base64

public fun <T> __getBasicCredential(req: HttpRequest<T>): BasicCredential? {
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

public fun <T> __getJwtTokenCredential(req: HttpRequest<T>): JwtCredential? {
    val authorizationHeader = req.headers["authorization"] ?: return null
    val prefix = "Bearer "
    if (!authorizationHeader.startsWith(prefix, ignoreCase = true)) return null
    val token = authorizationHeader.removePrefix(prefix).trim()
    if (token.isEmpty()) return null
    return JwtCredential(token)
}

public fun <T> __getTokenCredential(req: HttpRequest<T>, scheme: String): String? {
    val authorizationHeader = req.headers["authorization"] ?: return null
    val prefix = "$scheme "
    if (!authorizationHeader.startsWith(prefix, ignoreCase = true)) return null
    val token = authorizationHeader.removePrefix(prefix).trim()
    if (token.isEmpty()) return null
    return token
}

public fun <T> __getBearerTokenCredential(req: HttpRequest<T>): BearerCredential? {
    val token = __getTokenCredential(req, "Bearer")
    return if (token == null) null else BearerCredential(token)
}

///////////////////////////////////////

public fun <T> __unAuthorized(request: HttpRequest<T>): HttpResponse =
    request.respond("UNAUTHORIZED", HttpStatus.UNAUTHORIZED)