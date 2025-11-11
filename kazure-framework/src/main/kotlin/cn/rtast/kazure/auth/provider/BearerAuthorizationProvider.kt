/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BearerCredential

/**
 * Bearer token authorization provider
 * Any string in authorization header
 */
public interface BearerAuthorizationProvider : AuthorizationConfigure<BearerCredential> {
    override fun verify(request: HttpRequest<*>, context: HttpContext, credential: BearerCredential): Boolean
}