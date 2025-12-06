/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package test.test.next

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential
import cn.rtast.kazure.auth.provider.BasicAuthorizationProvider
import cn.rtast.kazure.next.*
import cn.rtast.kazure.next.annotations.Routing
import cn.rtast.kazure.response.respondText


object Auth : BasicAuthorizationProvider {
    override fun verify(
        request: HttpRequest<*>,
        context: HttpContext,
        credential: BasicCredential,
    ): Boolean = true

}

@Routing
val testRouting = (HttpMethod.POST and HttpMethod.GET) at
        "/api/" requiring Auth with setOf("path", "name") handledBy
        RequestHandler<String> { req, ctx, params ->
            req.respondText("")
        }

fun main() {
    (HttpMethod.POST and HttpMethod.GET) at "/api/" requiring Auth with setOf("path") handledBy RequestHandler<String> { req, ctx, params ->
        req.respondText("")
    }
}