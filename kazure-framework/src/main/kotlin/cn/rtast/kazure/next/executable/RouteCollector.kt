/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/12/7
 */


package cn.rtast.kazure.next.executable

import cn.rtast.kazure.next.RegisteredRouteSpec
import cn.rtast.kazure.util.toJson
import java.io.File

internal object RouteCollector {
    private val routes: MutableList<String> = mutableListOf()

    internal fun <T> register(spec: RegisteredRouteSpec<T>) = routes.add(spec.toJson())

    @JvmStatic
    fun exportAll(): String = routes.toJson()

    @JvmStatic
    fun main(args: Array<String>) {
        val data = this.exportAll()
        val output = "./metadata.json"
        File(output).apply {
            parentFile.mkdirs()
            writeText(data)
        }
    }
}