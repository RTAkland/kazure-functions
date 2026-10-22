/*
 * Copyright © 2026 rtakland
 * Author: rtakland
 * Date: 2026-10-22
 */


package cn.rtast.kazure.response

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.negotiation.BodyConverterRegistry
import cn.rtast.kazure.negotiation.MediaType

public inline fun <reified T> HttpRequest.receiveFromBytes(
    bytes: ByteArray, registry: BodyConverterRegistry = BodyConverterRegistry.DefaultConverters,
): T = registry.decode(bytes, MediaType.parse(headers["content-type"]))

public inline fun <reified T> HttpRequest.receive(
    registry: BodyConverterRegistry = BodyConverterRegistry.DefaultConverters,
): T = registry.decode<T>(body, MediaType.parse(headers["content-type"]))

public fun HttpRequest.bodyAsBytes(): ByteArray = body
    ?: throw IllegalStateException("No contents in body")

public fun HttpRequest.bodyAsText(): String = bodyAsBytes().decodeToString()