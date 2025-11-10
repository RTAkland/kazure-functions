/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest

public interface AuthorizationConfigure<C> {
    public fun verify(request: HttpRequest<*>, context: HttpContext, credential: C): Boolean
}