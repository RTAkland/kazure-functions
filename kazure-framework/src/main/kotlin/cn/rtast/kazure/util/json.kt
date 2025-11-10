/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/10/25
 */


package cn.rtast.kazure.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

public val kazureInternalGsonDoNotUseThis: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

public fun Any.toJson(): String {
    return kazureInternalGsonDoNotUseThis.toJson(this)
}

public inline fun <reified T> String.fromJson(): T {
    return kazureInternalGsonDoNotUseThis.fromJson(this, T::class.java)
}

public inline fun <reified T> String.fromArrayJson(): T {
    return kazureInternalGsonDoNotUseThis.fromJson(this, object : TypeToken<T>() {}.type)
}