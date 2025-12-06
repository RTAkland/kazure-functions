/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/6
 */


package cn.rtast.kazure.next

import cn.rtast.kazure.HttpMethod
import cn.rtast.kazure.auth.credentials.BaseCredential
import cn.rtast.kazure.auth.provider.AuthorizationConfigure


/**
 * The Router object, contains a function metadata
 * for generating azure functions
 */
public class Router(
    public val methods: List<HttpMethod>,
    public var route: String? = null,
    public val params: MutableSet<String> = mutableSetOf(),
    public var authProvider: AuthorizationConfigure<out BaseCredential>? = null,
)

/**
 * create a [Router]
 */
public infix fun HttpMethod.and(other: HttpMethod): Router =
    Router(listOf(this, other))


/**
 * create a [Router] with multiple [HttpMethod]
 */
public infix fun Router.and(other: HttpMethod): Router =
    Router(this.methods + other)

/**
 * specify route path
 */
public infix fun Router.at(route: String): Router {
    this.route = route
    return this
}

/**
 * set param
 */
public infix fun Router.with(param: String): Router {
    this.params += param
    return this
}

/**
 * set params at a time
 */
public infix fun Router.with(params: Set<String>): Router {
    this.params += params
    return this
}

/**
 * set auth basic provider
 */
public infix fun Router.requiring(auth: AuthorizationConfigure<out BaseCredential>): Router {
    this.authProvider = auth
    return this
}

/**
 * set routing handler
 */
public infix fun <T> Router.handledBy(handler: RequestHandler<T>): RegisteredRoute<T> = RegisteredRoute(
    route?.removePrefix("/") ?: error("Route must exists"),
    methods, params, authProvider, handler
)


