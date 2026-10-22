/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest

/**
 * Base authorization provider
 */
public sealed interface AuthorizationConfigure<T> {
    context(request: HttpRequest<*>, context: HttpContext, credential: T)
    public fun verify(): Boolean
}