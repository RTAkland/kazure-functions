/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/8/25
 */


package cn.rtast.kazure.util

@Suppress("UNCHECKED_CAST")
public fun <T> extractPathParams(
    uriTemplate: String,
    realUri: String,
    variableName: String
): T {
    val variableRegex = "\\{([^}]+)}".toRegex()
    val variables = variableRegex.findAll(uriTemplate).map { it.groupValues[1] }.toList()
    val regexPattern = Regex.escape(uriTemplate).replace("\\{[^}]+}".toRegex(), "([^/]+)")
    val regex = Regex("^$regexPattern$")
    val matchResult = regex.matchEntire(realUri)!!
    val index = variables.indexOf(variableName)
    return matchResult.groupValues.getOrNull(index + 1) as T
}
