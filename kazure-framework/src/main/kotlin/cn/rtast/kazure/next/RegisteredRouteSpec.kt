/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.next

import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure

@ConsistentCopyVisibility
public data class RegisteredRouteSpec<T> internal constructor(
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

