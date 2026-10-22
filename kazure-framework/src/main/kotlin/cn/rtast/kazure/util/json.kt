/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/10/25
 */


package cn.rtast.kazure.util

import kotlinx.serialization.json.Json

public val kazureInternalJson: Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = false
    classDiscriminator = "_json_type_"
    encodeDefaults = true
    coerceInputValues = true
    decodeEnumsCaseInsensitive = true
    isLenient = true
}

public inline fun <reified T> T.toJson(): String {
    return kazureInternalJson.encodeToString(this)
}

public inline fun <reified T> String.fromJson(): T {
    return kazureInternalJson.decodeFromString<T>(this)
}