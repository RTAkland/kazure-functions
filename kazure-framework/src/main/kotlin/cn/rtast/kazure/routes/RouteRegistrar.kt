/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/4
 */

@file:Suppress("unused")

package cn.rtast.kazure.routes

import cn.rtast.kazure.KAzureApplication
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure

public fun <T> KAzureApplication.registerRoute(
    route: String,
    params: List<String> = listOf(),
    ap: AuthorizationConfigure<BaseCredential>? = null,
    block: RequestBlock<T, BaseCredential>,
): RequestBlock<T, BaseCredential> = run { block }