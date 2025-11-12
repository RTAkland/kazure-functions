/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 11/11/25
 */


package cn.rtast.kazure.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import java.util.*

fun getMimeType(filename: String): String {
    val ext = filename.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "html", "htm" -> "text/html; charset=utf-8"
        "css" -> "text/css; charset=utf-8"
        "js" -> "application/javascript; charset=utf-8"
        "json" -> "application/json; charset=utf-8"
        "xml" -> "application/xml; charset=utf-8"
        "svg" -> "image/svg+xml; charset=utf-8"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        "ico" -> "image/x-icon"
        "bmp" -> "image/bmp"
        "avif" -> "image/avif"
        "mp3" -> "audio/mpeg"
        "wav" -> "audio/wav"
        "ogg" -> "audio/ogg"
        "mp4" -> "video/mp4"
        "webm" -> "video/webm"
        "pdf" -> "application/pdf"
        "zip" -> "application/zip"
        "gz" -> "application/gzip"
        "tar" -> "application/x-tar"
        "rar" -> "application/vnd.rar"
        "7z" -> "application/x-7z-compressed"
        "ttf" -> "font/ttf"
        "otf" -> "font/otf"
        "woff" -> "font/woff"
        "woff2" -> "font/woff2"
        "txt" -> "text/plain; charset=utf-8"
        "csv" -> "text/csv; charset=utf-8"
        "md" -> "text/markdown; charset=utf-8"
        "wasm" -> "application/wasm"
        else -> "application/octet-stream"
    }
}

class StaticRoutingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        val staticAssets = resolver.getSymbolsWithAnnotation("cn.rtast.kazure.resources.StaticAssets")
            .filterIsInstance<KSPropertyDeclaration>()
        val entries = mutableMapOf<String, String>()
        for (s in staticAssets) {
            if (s.type.resolve().declaration.qualifiedName?.asString() == "kotlin.ByteArray") {
                val annotation = s.annotations.find {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == "cn.rtast.kazure.resources.StaticAssets"
                }!!
                val routeArgValue = annotation.arguments.find { it.name?.asString() == "route" }!!.value as String
                val resArg = annotation.arguments.find { it.name?.asString() == "res" }!!.value as String
                val resArgValue = if (resArg == $$"$DEFAULT") routeArgValue else resArg
                entries[routeArgValue] = resArgValue
            } else {
                logger.error("StaticAssets type must be ByteArray -> ${s.qualifiedName?.asString()}")
                continue
            }
        }
        val fileContent = StringBuilder().apply {
            appendLine("package kazure.generated.a.b.c.d.e.f.g.h.i.j.k")
            appendLine("import cn.rtast.kazure.response.respondBytes")
        }
        entries.forEach { (route, res) ->
            val funcName = "assets_${UUID.randomUUID().toString().replace("-", "")}".substring(0, 12)
            val functionBody = """
                |@cn.rtast.kazure.trigger.HttpRouting("$route")
                |public fun $funcName
                |(req: com.microsoft.azure.functions.HttpRequestMessage<kotlin.Any?>,
                |ctx: com.microsoft.azure.functions.ExecutionContext): com.microsoft.azure.functions.HttpResponseMessage
                |= req.respondBytes(cn.rtast.kazure.resources.Resources.readBytes("$res"),
                |headers = mapOf("Content-Type" to "${getMimeType(res)}"))
                """.trimMargin()
            fileContent.appendLine(functionBody)
        }
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "kazure.generated.a.b.c.d.e.f.g.h.i.j.k",
            "staticAssets"
        )
        file.write(fileContent.toString().toByteArray())
        return emptyList()
    }
}