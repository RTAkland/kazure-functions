/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */

@file:Suppress("FunctionName")

package cn.rtast.kazure.auth.provider.func

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.InternalCompilerApi
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.credentials.HeaderCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure
import cn.rtast.kazure.response.respondText
import com.microsoft.azure.functions.HttpStatus
import kotlin.io.encoding.Base64

// All functions are used internal only

@InternalCompilerApi
public fun  __getBasicCredential(req: HttpRequest): BasicCredential? {
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

@InternalCompilerApi
public fun  __getHeaderCredential(req: HttpRequest, header: String): HeaderCredential? {
    val authorizationHeader = req.headers[header.lowercase()] ?: return null
    val token = authorizationHeader.trim()
    if (token.isEmpty()) return null
    return HeaderCredential(token)
}

@InternalCompilerApi
context(request: HttpRequest, httpContext: HttpContext, credential: T)
public fun <T : BaseCredential> AuthorizationConfigure<T>.__verifyWith(): Boolean = verify()

@InternalCompilerApi
public val HttpRequest.___u: HttpResponse get() = respondText("UNAUTHORIZED", status = HttpStatus.UNAUTHORIZED)
