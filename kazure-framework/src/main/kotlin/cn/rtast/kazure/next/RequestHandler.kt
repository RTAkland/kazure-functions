/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.next

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse

public fun interface RequestHandler<T> {
    public fun handle(req: HttpRequest<T>, ctx: HttpContext, params: Map<String, String>): HttpResponse
}