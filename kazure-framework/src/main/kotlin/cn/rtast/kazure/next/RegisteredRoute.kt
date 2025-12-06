/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.next

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider
import cn.rtast.kazure.auth.provider.BearerAuthorizationProvider
import cn.rtast.kazure.auth.provider.func.__getBasicCredential
import cn.rtast.kazure.auth.provider.func.__getBearerTokenCredential
import cn.rtast.kazure.auth.provider.func.__unAuthorized

public data class RegisteredRoute<T>(
    val route: String,
    val methods: List<HttpMethod>,
    val params: MutableSet<String>,
    val auth: AuthorizationConfigure<out BaseCredential>?,
    val handler: RequestHandler<T>,
)

/**
 *     public fun execute(req: HttpRequest<T>, ctx: HttpContext): HttpResponse {
 *         when (auth) {
 *             is BasicAuthorizationProvider -> {
 *                 val cred = __getBasicCredential(req) ?: return __unAuthorized(req)
 *                 if (!auth.verify(req, ctx, cred)) return __unAuthorized(req)
 *             }
 *
 *             is BearerAuthorizationProvider -> {
 *                 val cred = __getBearerTokenCredential(req) ?: return __unAuthorized(req)
 *                 if (!auth.verify(req, ctx, cred)) return __unAuthorized(req)
 *             }
 *
 *             null -> return handler.handle(req, ctx, ...)
 *         }
 *         return handler.handle(req, ctx, ...)
 *     }
 */