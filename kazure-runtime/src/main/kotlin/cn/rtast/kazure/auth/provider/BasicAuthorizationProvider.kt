/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/9/25
 */


package cn.rtast.kazure.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.auth.credentials.BasicCredential

/**
 * Basic authorization provider
 * Usually struct `<username>:<password>`
 */
public abstract class BasicAuthorizationProvider : AuthorizationConfigure<BasicCredential> {
    context(request: HttpRequest, context: HttpContext, credential: BasicCredential)
    public abstract override fun verify(): Boolean
}