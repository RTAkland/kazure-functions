/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/4
 */


package cn.rtast.kazure.routes

import cn.rtast.kazure.auth.credentials.BaseCredential

public data class RequestContext<T : BaseCredential>(
    val credential: T,
    val params: Map<String, String>,
) {
    public fun get(key: String): String? = params[key]
}