/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/4
 */


package cn.rtast.kazure.routes

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.KAzureApplication
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider
import cn.rtast.kazure.auth.provider.BearerAuthorizationProvider
import cn.rtast.kazure.auth.provider.func.__getBasicCredential
import cn.rtast.kazure.auth.provider.func.__getBearerTokenCredential
import cn.rtast.kazure.response.respondJson

@Suppress("FunctionName")
public fun <T> ___processRoute(
    req: HttpRequest<T>,
    ctx: HttpContext,
    ap: AuthorizationConfigure<BaseCredential>?,
    params: List<String>,
    block: RequestBlock<T, BaseCredential>,
): Pair<Boolean, BaseCredential?> {
    val result = when (ap) {
        is BasicAuthorizationProvider -> {
            val credential = __getBasicCredential(req) ?: return false to null
            ap.verify(req, ctx, credential) to credential
        }

        is BearerAuthorizationProvider -> {
            val credential = __getBearerTokenCredential(req) ?: return false to null
            ap.verify(req, ctx, credential) to credential
        }

        else -> true to null
    }
    return result
//    fun _unAuth() = req.respondText("Unauthorized", HttpStatus.UNAUTHORIZED)
//    return if (!result.first) _unAuth() else {
//        val rc: RequestContext<BaseCredential> = RequestContext(result.second, reqCtx.params)
//        block.invoke(req, rc)
//    }
}

public fun main() {
    val a = object : KAzureApplication {}.registerRoute<String>("/api/hello") {
        respondJson("")
    }
    a.invoke()
}
