/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.trigger

import com.microsoft.azure.functions.annotation.Cardinality

/**
 * Mark a function as eventHub routing entrypoint
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class EventHubRouting(
    val eventHubName: String,
    val connection: String,
    val cardinality: Cardinality = Cardinality.MANY,
    val consumerGroup: String = $$"$Default"
)