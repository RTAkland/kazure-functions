/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.trigger

/**
 * Mark a function as blob routing entrypoint
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class BlobRouting(
    val path: String,
    val connection: String = "",
    val source: String = "",
)