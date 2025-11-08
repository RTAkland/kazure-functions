/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */

package cn.rtast.kazure.functions

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse

public abstract class HttpAzureFunction : AzureFunction {
    public override var __request: HttpRequest? = null
    public abstract fun httpEntrypoint(
        request: HttpRequest,
        context: HttpContext,
    ): HttpResponse
}