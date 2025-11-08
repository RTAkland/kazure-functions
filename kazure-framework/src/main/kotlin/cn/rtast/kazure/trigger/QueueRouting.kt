/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.trigger

/**
 * name = msg
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class QueueRouting(val queueName: String, val connection: String = "")