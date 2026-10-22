/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure.resources

import kotlin.reflect.KProperty


/**
 * Mark a property as static assets routing
 * @param route route path
 * @param res path from jar. Same as route by default
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
public annotation class StaticAssets(val route: String, val res: String = $$"$DEFAULT")

/**
 * A property delegation no effects in literal code
 * When compiling, the compiler plugin will replace
 * IR tree, that auto register a routing for responding
 * static assets from jar.
 *
 * Usage example:
 * ```kotlin
 * @StaticAssets("index.html")
 * val anyNameHere: ByteArray by staticAssets
 */
@Suppress("ClassName")
public object staticAssets {
    public inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        error("staticAssets placeholder — should be replaced by KCP IR transformation")
    }
}