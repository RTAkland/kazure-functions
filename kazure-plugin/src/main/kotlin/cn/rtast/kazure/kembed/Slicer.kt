/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 9/24/25
 */


@file:OptIn(ExperimentalEncodingApi::class)

package cn.rtast.kazure.kembed

import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private fun File.slice(chunkSize: Int = 64 * 1024, compression: Boolean): List<ByteArray> {
    val data = if (compression) this.readBytes().deflate() else this.readBytes()
    if (data.size <= chunkSize) return listOf(data)
    val result = mutableListOf<ByteArray>()
    var offset = 0
    while (offset < data.size) {
        val end = (offset + chunkSize).coerceAtMost(data.size)
        result.add(data.copyOfRange(offset, end))
        offset += chunkSize
    }
    return result
}

fun List<File>.generateIndex(
    output: File,
    packageName: String,
    visibility: String = "internal",
    chunkSize: Int = 64 * 1024,
    compression: Boolean = false,
) {
    val indexFile = File(output, "resource_index.kt")
    val indexContent = StringBuilder().apply {
        appendLine("package $packageName")
        appendLine("import kotlin.reflect.KProperty")
        appendLine("import cn.rtast.kazure.resources.kembed.Chunk")
        appendLine("import cn.rtast.kazure.resources.kembed.Resource")
        appendLine("private const val isCompression = $compression")
        appendLine("$visibility val resourceIndex: Map<String, Chunk> = mapOf(")
    }
    this.forEach { inputDir ->
        inputDir.walk().filter { it.isFile }.forEach { file ->
            val baseName = file.name.sanitize()
            val slices = file.slice(chunkSize, compression)
            val sliceVarNames = slices.toKtFiles(output, packageName, baseName, visibility)
            indexContent.appendLine("    \"${file.relativeTo(inputDir).path.replace("\\", "/")}\" to Chunk(")
            indexContent.appendLine("        name = \"${file.name}\",")
            indexContent.appendLine("        chunks = listOf(")
            sliceVarNames.forEach { sliceName ->
                indexContent.appendLine("            $sliceName,")
            }
            indexContent.appendLine("        )")
            indexContent.appendLine("    ),")
        }
    }
    indexContent.appendLine(")")
    indexContent.apply {
        appendLine("public fun getResource(path: String): Resource {")
        appendLine("val chunk = requireNotNull(resourceIndex[path]) { throw IllegalStateException(\"资源不存在 \$path\") }")
        appendLine("return Resource(chunk.chunks, isCompression)")
        appendLine("}")
    }
    indexContent.append($$"""
        /**
         * Property delegate to load resource files by property name and type
         *
         * Usage example:
         * ```
         * // variable name can not contains "." character
         * val `indexHtml`: String by resources
         * val `test-contentBin`: ByteArray by resources
         * // Use this way
         * val `test-content`: ByteArray by resources("test-content.bin")
         * ```
         *
         * The property name is used as the resource path
         * The property type decides which method from [Resources] is called:
         * - [String] properties will load text content
         * - [ByteArray] properties will load raw bytes
         *
         * @throws IllegalArgumentException if type is not [ByteArray] or [String]
         */
        @Suppress("ClassName")
        public object resources {
            public class ResourcesDelegate(public val resourceName: String?) {
                public inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
                    val name = resourceName ?: property.name
                    val res = getResource(name)
                    return when (T::class) {
                        ByteArray::class -> res.asByteArray() as T
                        String::class -> res.asString() as T
                        else -> throw IllegalArgumentException("Unsupported property type ${T::class}")
                    }
                }
            }
            public inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
                return ResourcesDelegate(null).getValue(thisRef, property)
            }

            public operator fun invoke(resourceName: String): ResourcesDelegate = ResourcesDelegate(resourceName)
        }
    """.trimIndent())
    indexFile.writeText(indexContent.toString())
}

fun List<ByteArray>.toKtFiles(
    outputDir: File,
    packageName: String,
    baseName: String,
    visibility: String = "internal",
): List<String> {
    val variableNames = mutableListOf<String>()
    this.forEachIndexed { index, bytes ->
        val variableName = "${baseName}_$index"
        variableNames.add(variableName)
        val subDir = File(outputDir, variableName.take(2))
        subDir.mkdirs()
        val ktFile = File(subDir, "$variableName.kt")
        val base64 = Base64.encode(bytes)
        ktFile.writeText(
            """
            package $packageName
            internal val $variableName = "$base64"
            """.trimIndent()
        )
    }
    return variableNames
}