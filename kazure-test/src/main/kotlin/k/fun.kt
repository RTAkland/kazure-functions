/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package k

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.functions.HttpAzureFunction
import cn.rtast.kazure.respondResource
import cn.rtast.kazure.trigger.HttpRouting

class MyFunction : HttpAzureFunction() {
//    @PathParam("name", String::class)
    @HttpRouting("time")
    override fun httpEntrypoint(
        request: HttpRequest,
        context: HttpContext,
    ): HttpResponse {
        println()
        return request.respondResource("test-content.txt")
    }

    @HttpRouting("time2")
    fun e2(request: HttpRequest, context: HttpContext): HttpResponse {
        return request.respondResource("test-content.txt")
    }
}

