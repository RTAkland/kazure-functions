/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.HeaderCredential

/**
 * Bearer token authorization provider
 * Any string in authorization header
 */
public abstract class HeaderAuthorizationProvider(public val header: String = "authorization") :
    AuthorizationConfigure<HeaderCredential> {
    init {
        require(header.isNotEmpty()) { "Header based authorization provider value should not be empty" }
    }

    context(request: HttpRequest, context: HttpContext, credential: HeaderCredential)
    public abstract override fun verify(): Boolean
}