/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.v2.auth.provider

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.v2.auth.credential.BearerCredential

public interface BearerAuthProvider<T> : BaseAuthProvider<T, BearerCredential> {
    override fun verify(request: HttpRequest<T>, context: HttpContext, credential: BearerCredential)
}