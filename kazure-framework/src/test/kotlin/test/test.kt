/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package test

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.functions.HttpAzureFunction
import cn.rtast.kazure.respondText

class Fun1: HttpAzureFunction<String>(){
    override fun httpEntrypoint(
        request: HttpRequest<String>,
        context: HttpContext,
    ): HttpResponse {
        return request.respondText("1")
    }
}