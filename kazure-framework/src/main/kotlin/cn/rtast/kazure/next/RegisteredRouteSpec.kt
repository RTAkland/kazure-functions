/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kazure.next

import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure
import cn.rtast.kazure.next.serialization.AuthProviderSerializer
import cn.rtast.kazure.next.serialization.HandlerSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ConsistentCopyVisibility
public data class RegisteredRouteSpec<T> internal constructor(
    val route: String,
    val methods: List<HttpMethod>,
    val params: MutableSet<String>,
    @Serializable(with = AuthProviderSerializer::class)
    val auth: AuthorizationConfigure<out BaseCredential>?,
    @Serializable(with = HandlerSerializer::class)
    val handler: RequestHandler<T>,
) {
    val name: String = "f_${Uuid.random().toString().replace("-", "").substring(0, 10)}"
}

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

