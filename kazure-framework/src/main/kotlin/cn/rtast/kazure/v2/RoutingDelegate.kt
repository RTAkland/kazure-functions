/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/24/25
 */


package cn.rtast.kazure.v2

import cn.rtast.kazure.AuthLevel
import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.v2.auth.credential.BaseCredential
import cn.rtast.kazure.v2.auth.provider.BaseAuthProvider
import kotlin.reflect.KProperty

public class RoutingDelegate<T>(
    private val route: String,
    private val methods: List<HttpMethod>,
    private val authLevel: AuthLevel,
    private val bindings: List<String>,
    private val authProvider: BaseAuthProvider<T, BaseCredential>? = null,
    private val block: RoutingContext<T>,
) {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): KAzureContext<T> {
        return KAzureContext(route, methods, authLevel, bindings, authProvider, block)
    }
}